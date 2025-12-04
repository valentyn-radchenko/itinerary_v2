package org.mohyla.auth;

import lombok.extern.slf4j.Slf4j;
import org.mohyla.auth.application.AuthService;
import org.mohyla.auth.application.dto.ApiResponse;
import org.mohyla.auth.application.dto.AuthResponse;
import org.mohyla.auth.application.dto.LoginRequest;
import org.mohyla.auth.application.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthRestController {

    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        try {
            log.info("Registration request received for username: {}", request.username());
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(new ApiResponse<>(true, response, null));
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Registration error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Internal server error"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        try {
            log.info("Login request received for username: {}", request.username());
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(new ApiResponse<>(true, response, null));
        } catch (IllegalArgumentException e) {
            log.warn("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Login error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Internal server error"));
        }
    }
}

