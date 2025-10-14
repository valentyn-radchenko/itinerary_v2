package org.mohyla.itinerary.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mohyla.itinerary.routes.application.RoutesService;
import org.mohyla.itinerary.routes.domain.models.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RouteApi.class)
class RouteApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoutesService routesService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateRoute_Success() throws Exception {
        Route routeRequest = new Route("Kyiv", "Lviv");
        Route expectedRoute = new Route("Kyiv", "Lviv");
        
        when(routesService.createRoute(anyString(), anyString())).thenReturn(expectedRoute);
        
        mockMvc.perform(post("/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(routeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startPoint").value("Kyiv"))
                .andExpect(jsonPath("$.endPoint").value("Lviv"));
        
        verify(routesService).createRoute("Kyiv", "Lviv");
    }

    @Test
    void testGetAllRoutes_Success() throws Exception {
        List<Route> expectedRoutes = Arrays.asList(
            new Route("Kyiv", "Lviv"),
            new Route("Lviv", "Kharkiv")
        );
        when(routesService.getAllRoutes()).thenReturn(expectedRoutes);
        
        mockMvc.perform(get("/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].startPoint").value("Kyiv"))
                .andExpect(jsonPath("$[1].startPoint").value("Lviv"));
        
        verify(routesService).getAllRoutes();
    }
}
