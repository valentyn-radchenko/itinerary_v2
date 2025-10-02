package org.mohyla.itinerary.tickets.domain.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id @GeneratedValue
    private Long id;

    private Long userId;   // referencing Users module
    private Long routeId;  // referencing Routes module

    private LocalDateTime purchaseTime;

    private String status; // e.g. PENDING, CONFIRMED, CANCELLED

    protected Ticket() {} // JPA

    public Ticket(Long userId, Long routeId) {
        this.userId = userId;
        this.routeId = routeId;
        this.purchaseTime = LocalDateTime.now();
        this.status = "PENDING";
    }

    public void confirm() {
        this.status = "CONFIRMED";
    }

    // getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getRouteId() { return routeId; }
    public LocalDateTime getPurchaseTime() { return purchaseTime; }
    public String getStatus() { return status; }
}
