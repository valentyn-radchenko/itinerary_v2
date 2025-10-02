package org.mohyla.payments.dto;

public record PaymentResponseMessage(
        Long paymentId,
        Long ticketId,
        Long userId,
        double amount,
        String status
) {}
