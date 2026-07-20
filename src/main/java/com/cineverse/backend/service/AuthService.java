package com.cineverse.backend.service;

import com.cineverse.backend.dto.AuthResponse;
import com.cineverse.backend.dto.LoginRequest;
import com.cineverse.backend.dto.RefreshRequest;
import com.cineverse.backend.dto.RegisterRequest;
import com.cineverse.backend.entity.RefreshToken;
import com.cineverse.backend.entity.Role;
import com.cineverse.backend.entity.User;
import com.cineverse.backend.exception.EmailAlreadyRegisteredException;
import com.cineverse.backend.exception.InvalidRefreshTokenException;
import com.cineverse.backend.repository.RefreshTokenRepository;
import com.cineverse.backend.repository.RoleRepository;
import com.cineverse.backend.repository.UserRepository;
import com.cineverse.backend.security.JwtService;
import com.cineverse.backend.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {

    private static final String DEFAULT_ROLE = "ROLE_CUSTOMER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyRegisteredException("An account with this email already exists");
        }

        Role defaultRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new IllegalStateException(DEFAULT_ROLE + " is not seeded"));

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setMobileNumber(request.mobileNumber());
        user.setRoles(Set.of(defaultRole));
        userRepository.save(user);

        return issueTokens(UserPrincipal.from(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        return issueTokens((UserPrincipal) authentication.getPrincipal());
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        String rawToken = request.refreshToken();
        if (!jwtService.isTokenValid(rawToken)) {
            throw new InvalidRefreshTokenException("Refresh token is invalid or expired");
        }

        RefreshToken stored = refreshTokenRepository.findByTokenHash(jwtService.hashToken(rawToken))
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is unknown"));

        if (stored.isRevoked()) {
            refreshTokenRepository.revokeAllActiveByUserId(stored.getUser().getId());
            throw new InvalidRefreshTokenException("Refresh token has already been used");
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return issueTokens(UserPrincipal.from(stored.getUser()));
    }

    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenRepository.findByTokenHash(jwtService.hashToken(request.refreshToken()))
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private AuthResponse issueTokens(UserPrincipal principal) {
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        RefreshToken entity = RefreshToken.builder()
                .user(userRepository.getReferenceById(principal.getId()))
                .tokenHash(jwtService.hashToken(refreshToken))
                .expiryDate(jwtService.refreshTokenExpiry())
                .build();
        refreshTokenRepository.save(entity);

        return new AuthResponse(accessToken, refreshToken, "Bearer");
    }
}
