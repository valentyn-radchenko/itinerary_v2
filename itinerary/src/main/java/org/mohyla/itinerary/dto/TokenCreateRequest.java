package org.mohyla.itinerary.dto;

import java.io.Serializable;

public record TokenCreateRequest(String clientId, String clientSecret) implements Serializable {}
