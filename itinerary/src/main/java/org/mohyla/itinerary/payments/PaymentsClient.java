package org.mohyla.itinerary.payments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mohyla.itinerary.dto.ApiResponse;
import org.mohyla.itinerary.dto.PaymentRequestMessage;
import org.mohyla.itinerary.dto.PaymentResponseMessage;
import org.mohyla.itinerary.exception.ServiceCommunicationException;
import org.mohyla.itinerary.tickets.domain.models.Ticket;
import org.mohyla.itinerary.tickets.domain.persistence.TicketRepository;
import org.mohyla.itinerary.utils.ServiceTokenManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
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

        log.debug("Sending payment request with service token");

        try {
            String json = objectMapper.writeValueAsString(message);
            jmsTemplate.convertAndSend("payments.requests", json, msg -> {
                msg.setStringProperty("Authorization", "Bearer " + token);
                msg.setStringProperty("ClientId", clientId);
                msg.setStringProperty("ClientSecret", clientSecret);
                msg.setJMSCorrelationID(UUID.randomUUID().toString());
                return msg;
            });
            log.info("Payment creation request sent via JMS for ticketId: {}", message.ticketId());
        }catch (JsonProcessingException e){
            log.error("Failed to serialize payment request: {}", e.getMessage(), e);
            throw new ServiceCommunicationException("Failed to send payment request", e);
        }catch (RuntimeException e){
            log.error("Failed to send payment request: {}", e.getMessage(), e);
            throw new ServiceCommunicationException("Failed to send payment request", e);
        }
    }

    @JmsListener(destination = "payments.responses", containerFactory = "topicListenerFactory")
    public void receivePaymentConfirmation(String message) throws JsonProcessingException {
        log.debug("Received payment response from payments service");
        ApiResponse<PaymentResponseMessage> response = objectMapper.readValue(message, new TypeReference<ApiResponse<PaymentResponseMessage>>(){});
        if(response.success()){
            log.info("Payment confirmed for ticketId: {}", response.data().ticketId());
            Long ticketId = response.data().ticketId();
            Optional<Ticket> t = ticketRepository.findById(ticketId);
            if(t.isPresent()){
                Ticket ticket = t.get();
                ticket.confirm();
                log.info("Ticket {} confirmed with status: {}", ticket.getId(), ticket.getStatus());
                ticketRepository.save(ticket);
            }else{
                log.warn("No ticket found with id: {}", ticketId);
            }
        }else{
            log.error("Payment failed: {}", response.error());
        }
    }
}
