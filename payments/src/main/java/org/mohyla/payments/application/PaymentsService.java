package org.mohyla.payments.application;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.mohyla.payments.application.exceptions.PaymentProcessingException;
import org.mohyla.payments.domain.models.Payment;
import org.mohyla.payments.domain.persistence.PaymentRepository;
import org.mohyla.payments.dto.PaymentRequestMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentsService {

    private final PaymentRepository paymentRepository;

    public PaymentsService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment createPayment(PaymentRequestMessage message) {
        if (message.amount() <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        if (message.paymentMethod() == null || message.paymentMethod().isBlank()) {
            throw new IllegalArgumentException("Payment method must be provided");
        }
        log.debug("Creating payment with description: {}", message.description());
        Payment payment = new Payment(
                message.userId(),
                message.amount(),
                message.description(),
                message.paymentMethod()
        );


        log.info("Processing payment for user: {}", message.userId());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentProcessingException("Payment processing interrupted", e);
        }

        payment.complete();
        paymentRepository.save(payment);
        log.info("Payment completed with status: {}", payment.getStatus());
        return payment;
    }

}
