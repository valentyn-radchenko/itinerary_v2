package org.mohyla.itinerary.payments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mohyla.itinerary.dto.ApiResponse;
import org.mohyla.itinerary.dto.PaymentRequestMessage;
import org.mohyla.itinerary.dto.PaymentResponseMessage;
import org.mohyla.itinerary.dto.TokenCreateRequest;
import org.mohyla.itinerary.tickets.domain.models.Ticket;
import org.mohyla.itinerary.tickets.domain.persistence.TicketRepository;
import org.mohyla.itinerary.utils.ServiceTokenManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentsClient {

    private final JmsTemplate jmsTemplate;
    private final ServiceTokenManager tokenManager;
    private final ObjectMapper objectMapper;

    private final String clientId;
    private final String clientSecret;

    private final TicketRepository ticketRepository;
    public PaymentsClient(JmsTemplate jmsTemplate, @Value("${ITINERARY_MAIN_SECRET}") String clientSecret, ServiceTokenManager tokenManager, ObjectMapper objectMapper, TicketRepository ticketRepository) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
        this.ticketRepository = ticketRepository;
        this.clientId = "itinerary-main";
        this.clientSecret = clientSecret;
        this.jmsTemplate.setPubSubDomain(false);
        this.tokenManager = tokenManager;
    }


    public void createPayment(PaymentRequestMessage message){
        String token = tokenManager.getToken();
        System.out.println("Token in main app payment client " + token);

        try {
            String json = objectMapper.writeValueAsString(message);
            jmsTemplate.convertAndSend("payments.requests", json, msg -> {
                msg.setStringProperty("Authorization", "Bearer " + token);
                msg.setStringProperty("ClientId", clientId);
                msg.setStringProperty("ClientSecret", clientSecret);
                msg.setJMSCorrelationID(UUID.randomUUID().toString());
                return msg;
            });
            System.out.println("Sent payment creation request via JMS");
        }catch (RuntimeException | JsonProcessingException e){
            System.out.println("Request failed: " + e.getMessage());
        }
    }

    @JmsListener(destination = "payments.responses", containerFactory = "topicListenerFactory")
    public void receivePaymentConfirmation(String message) throws JsonProcessingException {
        System.out.println("Message from payments: " + message);
        ApiResponse<PaymentResponseMessage> response = objectMapper.readValue(message, new TypeReference<ApiResponse<PaymentResponseMessage>>(){});
        if(response.success()){
            System.out.println("Ticket payment confirmed");
            Long ticketId = response.data().ticketId();
            Optional<Ticket> t = ticketRepository.findById(ticketId);
            if(t.isPresent()){
                Ticket ticket = t.get();
                ticket.confirm();
                ticketRepository.save(ticket);
            }else{
                System.out.println("No ticket with given id");
            }
        }else{
            System.out.println("Payment failed. " + response.error());
        }
    }
}
