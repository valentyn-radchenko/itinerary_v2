package org.mohyla.itinerary.tickets.application;

import org.mohyla.itinerary.tickets.dto.PaymentResponseMessage;
import org.mohyla.itinerary.tickets.dto.TicketCreatedMessage;
import org.mohyla.itinerary.tickets.domain.models.Ticket;
import org.mohyla.itinerary.tickets.domain.persistence.TicketRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class TicketsService {

    private final TicketRepository ticketRepository;
    private final ApplicationEventPublisher events;
    private final RestTemplate restTemplate;

    public TicketsService(TicketRepository ticketRepository, ApplicationEventPublisher events, RestTemplate restTemplate) {
        this.ticketRepository = ticketRepository;
        this.events = events;
        this.restTemplate = restTemplate;
    }

    public void createTicket(Long userId, Long routeId) {
        Ticket ticket = new Ticket(userId, routeId);
        ticketRepository.save(ticket);
        TicketCreatedMessage message = new TicketCreatedMessage(ticket.getId(), userId, routeId);
        PaymentResponseMessage response = restTemplate.postForObject("http://payments:8080/api/payments/ticket-created", message, PaymentResponseMessage.class);
        if (response != null && "COMPLETED".equals(response.status())) {
            ticket.confirm(); // e.g. custom method on your Ticket entity
            ticketRepository.save(ticket);
            System.out.println("Ticket " + ticket.getId() + " marked as PAID");
        } else {
            ticketRepository.save(ticket);
            System.out.println("Ticket " + ticket.getId() + " marked as FAILED");
        }
        //events.publishEvent(new TicketCreatedEvent(ticket.getId(), userId, routeId));
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
