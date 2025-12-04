package org.mohyla.payments;

import lombok.extern.slf4j.Slf4j;
import org.mohyla.payments.application.PaymentsService;
import org.mohyla.payments.domain.models.Payment;
import org.mohyla.payments.domain.persistence.PaymentRepository;
import org.mohyla.payments.dto.PaymentRequestMessage;
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
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        Long currentUserId = securityUtils.getCurrentUserId();
        
        return paymentRepository.findById(id)
                .map(payment -> {
                    // Users can only view their own payments
                    if (!payment.getUserId().equals(currentUserId)) {
                        log.warn("User {} attempted to access payment {} owned by user {}", 
                                currentUserId, id, payment.getUserId());
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("You can only view your own payments");
                    }
                    return ResponseEntity.ok(payment);
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
                    "success", true,
                    "message", "Payment created"
            ));
        } catch (Exception e) {
            log.error("Failed to create payment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}

