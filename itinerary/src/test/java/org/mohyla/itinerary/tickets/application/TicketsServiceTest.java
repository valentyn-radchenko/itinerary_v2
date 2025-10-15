package org.mohyla.itinerary.tickets.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mohyla.itinerary.payments.PaymentsClient;
import org.mohyla.itinerary.dto.PaymentRequestMessage;
import org.mohyla.itinerary.dto.PaymentResponseMessage;
import org.mohyla.itinerary.tickets.domain.models.Ticket;
import org.mohyla.itinerary.tickets.domain.persistence.TicketRepository;
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
class TicketsServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ApplicationEventPublisher events;

    @Mock
    private PaymentsClient paymentClient;

    @InjectMocks
    private TicketsService ticketsService;

    @Test
    void testCreateTicket_Success() {
        Long userId = 1L;
        Long routeId = 1L;
        PaymentResponseMessage paymentResponse = new PaymentResponseMessage(1L, userId, "COMPLETED");
        
        when(paymentClient.createPayment(any(PaymentRequestMessage.class)))
            .thenReturn(paymentResponse);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket ticket = invocation.getArgument(0);
            return ticket;
        });
        
        String result = ticketsService.createTicket(userId, routeId);
        
        assertThat(result).contains("Ticket created successfully");
        verify(ticketRepository).save(any(Ticket.class));
        verify(paymentClient).createPayment(any(PaymentRequestMessage.class));
    }

    @Test
    void testGetAllTickets_Success() {
        List<Ticket> expectedTickets = Arrays.asList(
            new Ticket(1L, 1L),
            new Ticket(2L, 2L)
        );
        when(ticketRepository.findAll()).thenReturn(expectedTickets);
        
        List<Ticket> result = ticketsService.getAllTickets();
        
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedTickets);
        verify(ticketRepository).findAll();
    }
}
