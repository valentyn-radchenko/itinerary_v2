package org.mohyla.auth.application.dto;


public record ApiResponse<T>(
        boolean success,
        T data,
        String error
)  {}
