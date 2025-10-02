package org.mohyla.itinerary.users.domain.events;

public record UserCreatedEvent(Long userId, String name, String email) {
}
