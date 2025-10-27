package org.mohyla.itinerary.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRequestMessage(Long userId, Long ticketId, double amount,
                                    String description,
                                    String paymentMethod,
                                    LocalDateTime timestamp
                                    ) {}

