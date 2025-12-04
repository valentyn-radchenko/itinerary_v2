package org.mohyla.auth.application.dto;

public record RegisterRequest(
        String username,
        String email,
        String password
) {}

