package org.mohyla.itinerary.tickets.application;

import lombok.extern.slf4j.Slf4j;
import org.mohyla.itinerary.payments.PaymentsClient;
import org.mohyla.itinerary.dto.PaymentRequestMessage;
import org.mohyla.itinerary.tickets.application.exceptions.TicketCreationException;
import org.mohyla.itinerary.tickets.domain.models.Ticket;
import org.mohyla.itinerary.tickets.domain.persistence.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TicketsService {

    private final TicketRepository ticketRepository;
    private final PaymentsClient paymentClient;
    public TicketsService(TicketRepository ticketRepository, PaymentsClient paymentClient) {
        this.ticketRepository = ticketRepository;
        this.paymentClient = paymentClient;
    }

    public String createTicket(Long userId, Long routeId) {

        try {
            Ticket ticket = new Ticket(userId, routeId);
            ticketRepository.save(ticket);
            log.info("Created ticket with id: {} for userId: {} and routeId: {}", ticket.getId(), userId, routeId);
            PaymentRequestMessage message = new PaymentRequestMessage(userId, ticket.getId(), 100, "Payment for a ticket", "VISA",
                    LocalDateTime.now()
            );
            paymentClient.createPayment(message);

            log.info("Ticket {} marked as Pending, payment request sent", ticket.getId());
            return "Ticket created successfully.";

        } catch (RuntimeException e) {
            throw new TicketCreationException("Failed to create ticket: " + e.getMessage(), e);
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
