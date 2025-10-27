package org.mohyla.payments.dto;

public record PaymentResponseMessage(
        Long paymentId,
        Long userId,
        Long ticketId,
        String status
) {}
