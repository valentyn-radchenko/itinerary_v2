package org.mohyla.itinerary.routes.application;

import org.mohyla.itinerary.routes.domain.models.Route;
import org.mohyla.itinerary.routes.domain.persistence.RouteRepository;
import org.mohyla.itinerary.routes.domain.events.RouteCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoutesService {

    private final RouteRepository routeRepository;
    private final ApplicationEventPublisher events;

    public RoutesService(RouteRepository routeRepository, ApplicationEventPublisher events) {
        this.routeRepository = routeRepository;
        this.events = events;
    }

    public Route createRoute(String start, String end) {
        Route route = new Route(start, end);
        routeRepository.save(route);

        events.publishEvent(new RouteCreatedEvent(route.getId(), route.getStartPoint(), route.getEndPoint()));
        return route;
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Optional<Route> getRoute(Long id) {
        return routeRepository.findById(id);
    }
}

