package org.mohyla.itinerary.payments.dto;

public record PaymentResponseMessage(
        Long paymentId,
        Long userId,
        String status
) {}

