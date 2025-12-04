package org.mohyla.auth.application.dto;

public record AuthResponse(
        Long userId,
        String username,
        String email,
        String token
) {}

