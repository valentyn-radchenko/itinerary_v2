package org.mohyla.itinerary.exception;

import lombok.extern.slf4j.Slf4j;
import org.mohyla.itinerary.dto.ApiResponse;
import org.mohyla.itinerary.tickets.application.exceptions.TicketCreationException;
import org.mohyla.itinerary.utils.exceptions.PdfGenerationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenExpiredException(TokenExpiredException ex) {
        log.error("Token expired: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, null, "Authentication token has expired"));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidTokenException(InvalidTokenException ex) {
        log.error("Invalid token: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, null, "Invalid authentication token"));
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthenticatedException(UnauthenticatedException ex) {
        log.error("User not authenticated: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, null, "User not authenticated"));
    }

    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidUserIdException(InvalidUserIdException ex) {
        log.error("Invalid user ID: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, null, "Invalid user information in token"));
    }

    @ExceptionHandler(ServiceCommunicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleServiceCommunicationException(ServiceCommunicationException ex) {
        log.error("Service communication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiResponse<>(false, null, "Failed to communicate with external service"));
    }

    @ExceptionHandler(TicketCreationException.class)
    public ResponseEntity<ApiResponse<Object>> handleTicketCreationException(TicketCreationException ex) {
        log.error("Ticket creation failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<ApiResponse<Object>> handlePdfGenerationException(PdfGenerationException ex) {
        log.error("PDF generation failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, null, "Failed to generate PDF"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, null, "An unexpected error occurred"));
    }
}

