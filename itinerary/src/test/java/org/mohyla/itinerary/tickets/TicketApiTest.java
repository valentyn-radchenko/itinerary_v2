package org.mohyla.itinerary.tickets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mohyla.itinerary.dto.ApiResponse;
import org.mohyla.itinerary.tickets.application.TicketsService;
import org.mohyla.itinerary.tickets.domain.models.Ticket;
import org.mohyla.itinerary.utils.HtmlToPdfConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketApi.class)
class TicketApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketsService ticketsService;

    @MockBean
    private HtmlToPdfConverter htmlToPdfConverter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateTicket_Success() throws Exception {
        Long userId = 1L;
        Long routeId = 1L;
        String successMessage = "Ticket created successfully. Payment completed.";
        
        when(ticketsService.createTicket(anyLong(), anyLong())).thenReturn(successMessage);
        
        mockMvc.perform(post("/tickets")
                .param("userId", userId.toString())
                .param("routeId", routeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(successMessage));
        
        verify(ticketsService).createTicket(userId, routeId);
    }

    @Test
    void testGetAllTickets_Success() throws Exception {
        List<Ticket> expectedTickets = Arrays.asList(
            new Ticket(1L, 1L),
            new Ticket(2L, 2L)
        );
        when(ticketsService.getAllTickets()).thenReturn(expectedTickets);
        
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[1].userId").value(2));
        
        verify(ticketsService).getAllTickets();
    }
}
