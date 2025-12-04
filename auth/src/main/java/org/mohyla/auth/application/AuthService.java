package org.mohyla.auth.application;

import lombok.extern.slf4j.Slf4j;
import org.mohyla.auth.application.dto.AuthResponse;
import org.mohyla.auth.application.dto.LoginRequest;
import org.mohyla.auth.application.dto.RegisterRequest;
import org.mohyla.auth.domain.models.User;
import org.mohyla.auth.domain.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.username());

        // Validate input
        if (request.username() == null || request.username().isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (request.password() == null || request.password().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        // Check if user already exists
        if (userRepository.existsByUsername(request.username())) {
            log.warn("Registration failed: username {} already exists", request.username());
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration failed: email {} already exists", request.email());
            throw new IllegalArgumentException("Email already exists");
        }

        // Create user
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: userId={}, username={}", user.getId(), user.getUsername());

        // Generate JWT token
        String token = jwtTokenProvider.generateUserToken(user.getId(), user.getUsername(), user.getEmail());

        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), token);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to login user: {}", request.username());

        // Find user
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> {
                    log.warn("Login failed: user {} not found", request.username());
                    return new IllegalArgumentException("Invalid username or password");
                });

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Login failed: invalid password for user {}", request.username());
            throw new IllegalArgumentException("Invalid username or password");
        }

        log.info("User logged in successfully: userId={}, username={}", user.getId(), user.getUsername());

        // Generate JWT token
        String token = jwtTokenProvider.generateUserToken(user.getId(), user.getUsername(), user.getEmail());

        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), token);
    }
}

