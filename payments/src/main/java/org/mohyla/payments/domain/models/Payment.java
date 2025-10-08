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

    private Long userId;
    private double amount;
    private String description;



    private String paymentMethod;
    private LocalDateTime timestamp;
    private String status; //

    protected Payment() {}

    public Payment(Long userId, double amount, String description, String paymentMethod) {
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
    }

    public void complete() {
        this.status = "COMPLETED";
    }

    // getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() {return paymentMethod;}
    public String getDescription(){return description;}
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
}


