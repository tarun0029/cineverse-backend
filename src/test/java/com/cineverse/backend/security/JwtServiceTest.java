package com.cineverse.backend.security;

import com.cineverse.backend.config.JwtProperties;
import com.cineverse.backend.entity.Role;
import com.cineverse.backend.entity.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "zTg5rDpCit8OH46ijGWJCwaBv8UsFPvlZyryBAEAVH8=";

    private UserPrincipal samplePrincipal() {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("hashed-password");
        user.setMobileNumber("9999999999");
        user.setRoles(Set.of(Role.builder().name("ROLE_CUSTOMER").build()));
        return UserPrincipal.from(user);
    }

    @Test
    void generatesAccessTokenThatRoundTripsClaims() {
        JwtService jwtService = new JwtService(new JwtProperties(SECRET, 900_000L, 604_800_000L));
        UserPrincipal principal = samplePrincipal();

        String token = jwtService.generateAccessToken(principal);

        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractEmail(token)).isEqualTo("test@example.com");
        assertThat(jwtService.extractUserId(token)).isEqualTo(1);
    }

    @Test
    void rejectsExpiredToken() {
        JwtService jwtService = new JwtService(new JwtProperties(SECRET, -1_000L, -1_000L));
        String token = jwtService.generateAccessToken(samplePrincipal());

        assertThat(jwtService.isTokenValid(token)).isFalse();
    }

    @Test
    void rejectsTamperedToken() {
        JwtService jwtService = new JwtService(new JwtProperties(SECRET, 900_000L, 604_800_000L));
        String token = jwtService.generateAccessToken(samplePrincipal());
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThat(jwtService.isTokenValid(tampered)).isFalse();
    }
}
