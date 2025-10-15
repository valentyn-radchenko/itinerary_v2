package org.mohyla.itinerary.routes.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mohyla.itinerary.routes.domain.events.RouteCreatedEvent;
import org.mohyla.itinerary.routes.domain.models.Route;
import org.mohyla.itinerary.routes.domain.persistence.RouteRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoutesServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private ApplicationEventPublisher events;

    @InjectMocks
    private RoutesService routesService;

    @Test
    void testCreateRoute_Success() {
        String startPoint = "Kyiv";
        String endPoint = "Lviv";
        Route expectedRoute = new Route(startPoint, endPoint);
        
        when(routeRepository.save(any(Route.class))).thenReturn(expectedRoute);
        
        Route result = routesService.createRoute(startPoint, endPoint);
        
        assertThat(result.getStartPoint()).isEqualTo(startPoint);
        assertThat(result.getEndPoint()).isEqualTo(endPoint);
        verify(routeRepository).save(any(Route.class));
        verify(events).publishEvent(any(RouteCreatedEvent.class));
    }

    @Test
    void testGetAllRoutes_Success() {
        List<Route> expectedRoutes = Arrays.asList(
            new Route("Kyiv", "Lviv"),
            new Route("Lviv", "Kharkiv")
        );
        when(routeRepository.findAll()).thenReturn(expectedRoutes);
        
        List<Route> result = routesService.getAllRoutes();
        
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedRoutes);
        verify(routeRepository).findAll();
    }
}
