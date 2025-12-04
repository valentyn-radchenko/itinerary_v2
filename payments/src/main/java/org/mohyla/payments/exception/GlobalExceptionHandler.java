package org.mohyla.payments.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String SUCCESS_KEY = "success";
    private static final String ERROR_KEY = "error";

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleTokenExpiredException(TokenExpiredException ex) {
        log.error("Token expired: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(SUCCESS_KEY, false, ERROR_KEY, "Authentication token has expired"));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTokenException(InvalidTokenException ex) {
        log.error("Invalid token: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(SUCCESS_KEY, false, ERROR_KEY, "Invalid authentication token"));
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthenticatedException(UnauthenticatedException ex) {
        log.error("User not authenticated: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(SUCCESS_KEY, false, ERROR_KEY, "User not authenticated"));
    }

    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUserIdException(InvalidUserIdException ex) {
        log.error("Invalid user ID: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(SUCCESS_KEY, false, ERROR_KEY, "Invalid user information in token"));
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentProcessingException(PaymentProcessingException ex) {
        log.error("Payment processing failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(SUCCESS_KEY, false, ERROR_KEY, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(Map.of(SUCCESS_KEY, false, ERROR_KEY, ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(SUCCESS_KEY, false, ERROR_KEY, "An unexpected error occurred"));
    }
}

