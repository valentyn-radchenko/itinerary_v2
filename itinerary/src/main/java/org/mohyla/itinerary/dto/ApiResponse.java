package org.mohyla.itinerary.dto;

import java.io.Serializable;

public record ApiResponse<T>(boolean success, T data, String error) implements Serializable {}
