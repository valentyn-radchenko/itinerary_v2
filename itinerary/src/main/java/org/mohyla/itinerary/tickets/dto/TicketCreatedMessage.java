package org.mohyla.itinerary.tickets.dto;

public record TicketCreatedMessage(Long ticketId, Long userId, Long routeId) {}

