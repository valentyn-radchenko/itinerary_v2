package org.mohyla.itinerary.routes.domain.persistence;

import org.mohyla.itinerary.routes.domain.models.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
}
