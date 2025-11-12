package org.mohyla.payments;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.hibernate.Filter;
import org.mohyla.payments.application.PaymentsService;
import org.mohyla.payments.domain.models.Payment;
import org.mohyla.payments.dto.ApiResponse;
import org.mohyla.payments.dto.PaymentResponseMessage;
import org.mohyla.payments.dto.PaymentRequestMessage;
import org.mohyla.payments.utils.JwtTokenValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentsController {

    private final PaymentsService paymentsService;
    private final JwtTokenValidator jwtTokenValidator;
    public PaymentsController(PaymentsService paymentsService, JwtTokenValidator jwtTokenValidator) {
        this.paymentsService = paymentsService;
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<PaymentResponseMessage>> handlePaymentRequest(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody PaymentRequestMessage message) {

        try {
            String token = extractToken(authorizationHeader);
            System.out.println("Token in payments controller" + token);
            Jws<Claims> claims = jwtTokenValidator.validate(token);

            Payment payment = paymentsService.createPayment(message);
            PaymentResponseMessage response = new PaymentResponseMessage(
                    payment.getId(),
                    payment.getUserId(),
                    payment.getStatus()
            );
            System.out.println("Payment status: " + payment.getStatus());
            System.out.println("Payment response status: " + response.status());
            return ResponseEntity.ok(new ApiResponse<>(true, response, null));

        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, null, "Invalid or expired token" + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Internal server error: " + e.getMessage()));
        }
    }
    private String extractToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new JwtException("Missing or invalid Authorization header");
        }
        return header.substring(7);
    }
}

