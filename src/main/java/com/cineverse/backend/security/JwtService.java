package com.cineverse.backend.security;

import com.cineverse.backend.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class JwtService {

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtService(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
        this.accessTokenExpirationMs = properties.accessTokenExpirationMs();
        this.refreshTokenExpirationMs = properties.refreshTokenExpirationMs();
    }

    public String generateAccessToken(UserPrincipal principal) {
        return buildToken(principal, accessTokenExpirationMs);
    }

    public String generateRefreshToken(UserPrincipal principal) {
        return buildToken(principal, refreshTokenExpirationMs);
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public Integer extractUserId(String token) {
        return parseClaims(token).get("userId", Integer.class);
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String buildToken(UserPrincipal principal, long expirationMs) {
        Instant now = Instant.now();
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("userId", principal.getId())
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
