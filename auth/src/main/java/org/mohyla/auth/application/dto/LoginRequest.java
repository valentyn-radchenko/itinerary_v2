package org.mohyla.auth.application.dto;

public record LoginRequest(
        String username,
        String password
) {}

