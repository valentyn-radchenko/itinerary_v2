package org.mohyla.payments.application;

import jakarta.transaction.Transactional;
import org.mohyla.payments.domain.models.Payment;
import org.mohyla.payments.domain.persistence.PaymentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentsService {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher events;

    public PaymentsService(PaymentRepository paymentRepository, ApplicationEventPublisher events) {
        this.paymentRepository = paymentRepository;
        this.events = events;
    }

    @Transactional
    public Payment createPayment(Long ticketId, Long userId, double amount) {
        Payment payment = new Payment(ticketId, userId, amount);
        paymentRepository.save(payment);

        System.out.println("Processing payment...");
        try {
            Thread.sleep(2000); // wait 5s
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        payment.complete();
        paymentRepository.save(payment);

        System.out.println("Payment Processed");
        return payment;
    }

}
