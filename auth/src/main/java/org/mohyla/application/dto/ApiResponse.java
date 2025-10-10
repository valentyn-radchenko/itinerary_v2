package org.mohyla.application.dto;

public record ApiResponse<T>(
        boolean success,
        T data,
        String error
) {}
