package org.mohyla.itinerary.tickets.domain.persistence;

import org.mohyla.itinerary.tickets.domain.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
