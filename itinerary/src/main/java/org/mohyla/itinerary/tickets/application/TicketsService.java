package org.mohyla.itinerary.tickets.application;

import org.mohyla.itinerary.payments.PaymentsClient;
import org.mohyla.itinerary.dto.PaymentRequestMessage;
import org.mohyla.itinerary.tickets.domain.models.Ticket;
import org.mohyla.itinerary.tickets.domain.persistence.TicketRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketsService {

    private final TicketRepository ticketRepository;
    private final ApplicationEventPublisher events;
    private final PaymentsClient paymentClient;
    public TicketsService(TicketRepository ticketRepository, ApplicationEventPublisher events, PaymentsClient paymentClient) {
        this.ticketRepository = ticketRepository;
        this.events = events;
        this.paymentClient = paymentClient;
    }

    public String createTicket(Long userId, Long routeId) {
        Ticket ticket = new Ticket(userId, routeId);
        PaymentRequestMessage message = new PaymentRequestMessage(userId, ticket.getId(), 100, "Payment for a ticket", "VISA",
                LocalDateTime.now()
        );

        try {
            paymentClient.createPayment(message);
            ticketRepository.save(ticket);
            System.out.println("Ticket " + ticket.getId() + " marked as Pending");
            return "Ticket created successfully.";

        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to create ticket: " + e.getMessage(), e);
        }
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicket(Long id) {
        return ticketRepository.findById(id);
    }

    public void confirmTicket(Long ticketId) {
        ticketRepository.findById(ticketId).ifPresent(ticket -> {
            ticket.confirm();
            ticketRepository.save(ticket);
        });
    }
}
