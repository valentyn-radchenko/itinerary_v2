package org.mohyla.payments;

import org.mohyla.payments.application.PaymentsService;
import org.mohyla.payments.domain.models.Payment;
import org.mohyla.payments.dto.PaymentResponseMessage;
import org.mohyla.payments.dto.PaymentRequestMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentsController {

    private final PaymentsService paymentsService;

    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }
    @PostMapping("/")
    public ResponseEntity<PaymentResponseMessage> handleTicketCreated(@RequestBody PaymentRequestMessage message) {
        Payment payment = paymentsService.createPayment(message);
        PaymentResponseMessage response = new PaymentResponseMessage(
                payment.getId(),
                payment.getUserId(),
                payment.getStatus()
        );
        return ResponseEntity.ok(response);
    }
}

