package org.mohyla.payments.dto;

import java.time.LocalDateTime;

public record PaymentRequestMessage(Long userId, double amount,
                                    String description,
                                    String paymentMethod,
                                    LocalDateTime timestamp) {}
