package org.mohyla.itinerary.dto;

public record PaymentResponseMessage(
        Long paymentId,
        Long userId,
        String status
) {}

