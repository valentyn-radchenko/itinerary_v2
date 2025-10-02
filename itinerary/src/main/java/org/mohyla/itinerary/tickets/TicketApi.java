package org.mohyla.itinerary.tickets;

import org.mohyla.itinerary.tickets.application.TicketsService;
import org.mohyla.itinerary.tickets.domain.models.Ticket;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketApi {

    private final TicketsService ticketsService;

    public TicketApi(TicketsService ticketsService) {
        this.ticketsService = ticketsService;
    }

    @PostMapping
    public ResponseEntity<String> createTicket(@RequestParam Long userId, @RequestParam Long routeId) {
        ticketsService.createTicket(userId, routeId);
        return ResponseEntity.ok("Response sent");
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketsService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicket(@PathVariable Long id) {
        return ticketsService.getTicket(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

