package org.mohyla.itinerary.tickets.dto;

public record PaymentResponseMessage(
        Long paymentId,
        Long ticketId,
        Long userId,
        double amount,
        String status
) {}

