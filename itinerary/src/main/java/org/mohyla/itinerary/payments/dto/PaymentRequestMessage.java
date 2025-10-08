package org.mohyla.itinerary.payments.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRequestMessage(Long userId, double amount,
                                    String description,
                                    String paymentMethod,
                                    LocalDateTime timestamp
                                    ) {}

