package org.mohyla.itinerary.dto;

public record ApiResponse<T>(boolean success, T data, String error) {}
