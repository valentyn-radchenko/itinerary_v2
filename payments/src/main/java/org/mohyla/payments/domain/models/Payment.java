package org.mohyla.payments.domain.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id @GeneratedValue
    private Long id;

    private Long ticketId;
    private Long userId;
    private double amount;
    private LocalDateTime timestamp;
    private String status; // PENDING, COMPLETED, FAILED

    protected Payment() {}

    public Payment(Long ticketId, Long userId, double amount) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
    }

    public void complete() {
        this.status = "COMPLETED";
    }

    // getters
    public Long getId() { return id; }
    public Long getTicketId() { return ticketId; }
    public Long getUserId() { return userId; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
}


