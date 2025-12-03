package org.mohyla.itinerary.tickets.application.exceptions;

public class TicketCreationException extends RuntimeException {

    public TicketCreationException(String message) {
        super(message);
    }

    public TicketCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}

