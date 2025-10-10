package org.mohyla;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.mohyla.application.JwtTokenProvider;
import org.mohyla.application.dto.ApiResponse;
import org.mohyla.application.dto.TokenCreateRequest;
import org.mohyla.application.utils.ClientCredentialsValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/api")
public class AuthApi {

    private final JwtTokenProvider jwtTokenProvider;
    private final ClientCredentialsValidator credentialValidator;

    public AuthApi(JwtTokenProvider jwtTokenProvider, ClientCredentialsValidator credentialValidator) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.credentialValidator = credentialValidator;
    }

    @PostMapping("/jwt")
    public ResponseEntity<ApiResponse<String>> createToken(@RequestBody TokenCreateRequest request) {
        System.out.println("In auth api");
        try {
            if (request.clientId() == null || request.clientSecret() == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "clientId and clientSecret must be provided"));
            }

            if (!credentialValidator.validate(request.clientId(), request.clientSecret())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, null, "Invalid credentials"));
            }

            String token = jwtTokenProvider.generateServiceToken(request.clientId());

            return ResponseEntity.ok(new ApiResponse<>(true, token, null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to generate token: " + e.getMessage()));
        }
    }



}
