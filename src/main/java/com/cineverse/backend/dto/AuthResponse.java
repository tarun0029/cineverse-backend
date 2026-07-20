package com.cineverse.backend.dto;

public record AuthResponse(String accessToken, String refreshToken, String tokenType) {
}
