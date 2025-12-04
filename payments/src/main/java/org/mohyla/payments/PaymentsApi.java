package org.mohyla.payments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.jms.JMSException;
import lombok.extern.slf4j.Slf4j;
import org.mohyla.payments.application.PaymentsService;
import org.mohyla.payments.domain.models.Payment;
import org.mohyla.payments.dto.ApiResponse;
import org.mohyla.payments.dto.PaymentRequestMessage;
import org.mohyla.payments.dto.PaymentResponseMessage;
import org.mohyla.payments.security.JwtTokenValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentsApi {

    private final JmsTemplate topicJmsTemplate;
    private final PaymentsService paymentsService;
    private final JwtTokenValidator jwtTokenValidator;
    private final ObjectMapper objectMapper;
    public PaymentsApi(@Qualifier("topicJmsTemplate") JmsTemplate topicJmsTemplate, PaymentsService paymentsService, JwtTokenValidator jwtTokenValidator, ObjectMapper objectMapper) {
        this.topicJmsTemplate = topicJmsTemplate;
        this.paymentsService = paymentsService;
        this.jwtTokenValidator = jwtTokenValidator;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "payments.requests", containerFactory = "queueListenerFactory",
            selector = "ClientId = 'itinerary-main'")
    public void createPaymentListener(String message, jakarta.jms.Message jmsMessage) throws JsonProcessingException, JMSException {
        log.debug("Payment request received from itinerary-main");
        PaymentRequestMessage request = objectMapper.readValue(message, PaymentRequestMessage.class);
        log.debug("Processing payment request for ticketId: {}", request.ticketId());

        log.debug("Authorization header present: {}", jmsMessage.getStringProperty("Authorization") != null);
        String token = extractToken(jmsMessage.getStringProperty("Authorization"));

        log.debug("JWT token extracted successfully");

        try{
            jwtTokenValidator.validateToken(token);

            log.info("Processing payment for ticketId: {}", request.ticketId());
            Payment payment = paymentsService.createPayment(request);
            PaymentResponseMessage responseMessage = new PaymentResponseMessage(
                    payment.getId(),
                    payment.getUserId(),
                    request.ticketId(),
                    payment.getStatus()
            );
            log.info("Payment created successfully with status: {} for ticketId: {}", payment.getStatus(), request.ticketId());

            ApiResponse<PaymentResponseMessage> response = new ApiResponse<>(true, responseMessage, null);
            topicJmsTemplate.convertAndSend("payments.responses", objectMapper.writeValueAsString(response));
            log.debug("Payment response sent for paymentId: {}", responseMessage.paymentId());
        }catch (Exception e){
            log.error("Error processing payment for ticketId: {}: {}", request.ticketId(), e.getMessage(), e);
            ApiResponse<PaymentResponseMessage> response = new ApiResponse<>(false, null, e.getMessage());
            topicJmsTemplate.convertAndSend("payments.responses", objectMapper.writeValueAsString(response));
        }

    }
    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException("Missing Authorization header");
        }

        if (authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7).trim();
        }
        else{
            throw new IllegalArgumentException("Wrong token format");
        }
    }
}

