package org.mohyla.payments;

import org.mohyla.payments.application.PaymentsService;
import org.mohyla.payments.domain.models.Payment;
import org.mohyla.payments.dto.PaymentResponseMessage;
import org.mohyla.payments.dto.TicketCreatedMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentsController {

    private final PaymentsService paymentsService;

    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }
    @PostMapping("/ticket-created")
    public ResponseEntity<PaymentResponseMessage> handleTicketCreated(@RequestBody TicketCreatedMessage message) {
        Payment payment = paymentsService.createPayment(message.ticketId(), message.userId(), 100.0);
        PaymentResponseMessage response = new PaymentResponseMessage(
                payment.getId(),
                payment.getTicketId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus()
        );
        return ResponseEntity.ok(response);
    }
}

