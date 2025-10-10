package org.mohyla.payments.dto;

public record ApiResponse<T>(
        boolean success,
        T data,
        String error
) {}
