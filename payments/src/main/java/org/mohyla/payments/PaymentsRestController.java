package org.mohyla.payments;

import org.mohyla.payments.application.PaymentsService;
import org.mohyla.payments.domain.models.Payment;
import org.mohyla.payments.domain.persistence.PaymentRepository;
import org.mohyla.payments.dto.PaymentRequestMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentsRestController {

    private final PaymentRepository paymentRepository;
    private final PaymentsService paymentsService;

    public PaymentsRestController(PaymentRepository paymentRepository, PaymentsService paymentsService) {
        this.paymentRepository = paymentRepository;
        this.paymentsService = paymentsService;
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }

    // breaking changes - commented out to avoid mapping conflict
    // @GetMapping
    // public ResponseEntity<List<Payment>> getAllPaymentsBreaking() {
    //     return ResponseEntity.status(HttpStatus.CREATED)
    //             .body(paymentRepository.findAll());
    // }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return paymentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Double amount = ((Number) request.get("amount")).doubleValue();
            String description = (String) request.get("description");
            String paymentMethod = (String) request.get("paymentMethod");

            PaymentRequestMessage message = new PaymentRequestMessage(
                    userId, null, amount, description, paymentMethod, LocalDateTime.now()
            );

            paymentsService.createPayment(message);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment created"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}

