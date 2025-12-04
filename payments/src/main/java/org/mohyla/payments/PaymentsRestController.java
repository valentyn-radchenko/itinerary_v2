package org.mohyla.payments;

import lombok.extern.slf4j.Slf4j;
import org.mohyla.payments.application.PaymentsService;
import org.mohyla.payments.domain.models.Payment;
import org.mohyla.payments.domain.persistence.PaymentRepository;
import org.mohyla.payments.dto.PaymentRequestMessage;
import org.mohyla.payments.exception.PaymentProcessingException;
import org.mohyla.payments.exception.UnauthenticatedException;
import org.mohyla.payments.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/payments")
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*", allowedHeaders = "*")
public class PaymentsRestController {

    private static final String SUCCESS_KEY = "success";
    private static final String MESSAGE_KEY = "message";

    private final PaymentRepository paymentRepository;
    private final PaymentsService paymentsService;
    private final SecurityUtils securityUtils;

    public PaymentsRestController(PaymentRepository paymentRepository, 
                                  PaymentsService paymentsService,
                                  SecurityUtils securityUtils) {
        this.paymentRepository = paymentRepository;
        this.paymentsService = paymentsService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getMyPayments() {
        Long currentUserId = securityUtils.getCurrentUserId();
        log.info("Getting payments for userId={}", currentUserId);
        return ResponseEntity.ok(paymentRepository.findByUserId(currentUserId).orElse(List.of()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPaymentById(@PathVariable Long id) {
        Long currentUserId = securityUtils.getCurrentUserId();
        
        return paymentRepository.findById(id)
                .map(payment -> {
                    // Users can only view their own payments
                    if (!payment.getUserId().equals(currentUserId)) {
                        log.warn("User {} attempted to access payment {} owned by user {}", 
                                currentUserId, id, payment.getUserId());
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body((Object) "You can only view your own payments");
                    }
                    return ResponseEntity.ok((Object) payment);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Map<String, Object> request) {
        try {
            // Use current authenticated user's ID
            Long currentUserId = securityUtils.getCurrentUserId();
            
            Double amount = ((Number) request.get("amount")).doubleValue();
            String description = (String) request.get("description");
            String paymentMethod = (String) request.get("paymentMethod");

            log.info("Creating payment for userId={}, amount={}", currentUserId, amount);

            PaymentRequestMessage message = new PaymentRequestMessage(
                    currentUserId, null, amount, description, paymentMethod, LocalDateTime.now()
            );

            paymentsService.createPayment(message);

            return ResponseEntity.ok(Map.of(
                    SUCCESS_KEY, true,
                    MESSAGE_KEY, "Payment created"
            ));
        } catch (UnauthenticatedException e) {
            log.error("User not authenticated: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    SUCCESS_KEY, false,
                    MESSAGE_KEY, "User not authenticated"
            ));
        } catch (PaymentProcessingException e) {
            log.error("Payment processing failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    SUCCESS_KEY, false,
                    MESSAGE_KEY, e.getMessage()
            ));
        } catch (RuntimeException e) {
            log.error("Unexpected error creating payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    SUCCESS_KEY, false,
                    MESSAGE_KEY, "Internal error occurred"
            ));
        }
    }
}

