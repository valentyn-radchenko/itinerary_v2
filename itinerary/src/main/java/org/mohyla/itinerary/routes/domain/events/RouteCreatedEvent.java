package org.mohyla.itinerary.routes.domain.events;

public record RouteCreatedEvent(Long routeId, String startPoint, String endPoint) {}
