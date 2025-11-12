package org.mohyla.payments.application;

import jakarta.transaction.Transactional;
import org.mohyla.payments.domain.models.Payment;
import org.mohyla.payments.domain.persistence.PaymentRepository;
import org.mohyla.payments.dto.PaymentRequestMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentsService {

    private final PaymentRepository paymentRepository;

    public PaymentsService(PaymentRepository paymentRepository, ApplicationEventPublisher events) {
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
        System.out.println("Payment message description: " + message.description());
        Payment payment = new Payment(
                message.userId(),
                message.amount(),
                message.description(),
                message.paymentMethod()
        );


        System.out.println("Processing payment for user " + message.userId() + "...");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Payment processing interrupted");
        }

        payment.complete();
        paymentRepository.save(payment);
        System.out.println("Payment completed");
        System.out.println("Completed payment status: " + payment.getStatus());
        return payment;
    }

}
