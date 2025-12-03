package org.mohyla.auth.application.dto;

import java.io.Serializable;

public record ApiResponse<T>(
        boolean success,
        T data,
        String error
)  {}
