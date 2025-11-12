package org.mohyla.itinerary.routes;

import org.mohyla.itinerary.routes.application.RoutesService;
import org.mohyla.itinerary.routes.domain.models.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteApi {

    @Autowired
    private final RoutesService routesService;

    public RouteApi(RoutesService routesService) {
        this.routesService = routesService;
    }

    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody Route request) {
        Route created = routesService.createRoute(request.getStartPoint(), request.getEndPoint());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routesService.getAllRoutes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Route> getRoute(@PathVariable Long id) {
        return routesService.getRoute(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

