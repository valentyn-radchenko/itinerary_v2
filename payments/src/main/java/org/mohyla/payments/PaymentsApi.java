package org.mohyla.payments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.jms.JMSException;
import org.mohyla.payments.application.PaymentsService;
import org.mohyla.payments.domain.models.Payment;
import org.mohyla.payments.dto.ApiResponse;
import org.mohyla.payments.dto.PaymentRequestMessage;
import org.mohyla.payments.dto.PaymentResponseMessage;
import org.mohyla.payments.utils.JwtTokenValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

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
        System.out.println("Message from itinerary-main");
        PaymentRequestMessage request = objectMapper.readValue(message, PaymentRequestMessage.class);
        String token = jmsMessage.getStringProperty("Authorization");
        String clientId = jmsMessage.getStringProperty("ClientId");
        String clientSecret = jmsMessage.getStringProperty("ClientSecret");
        String correlationId = jmsMessage.getJMSCorrelationID();

        try{
            Jws<Claims> claims = jwtTokenValidator.validate(token);


            Payment payment = paymentsService.createPayment(request);
            PaymentResponseMessage responseMessage = new PaymentResponseMessage(
                    payment.getId(),
                    request.ticketId(),
                    payment.getUserId(),
                    payment.getStatus()
            );
            System.out.println("Payment status: " + payment.getStatus());
            System.out.println("Payment response status: " + responseMessage.status());

            ApiResponse<PaymentResponseMessage> response = new ApiResponse<>(true, responseMessage, null);
            topicJmsTemplate.convertAndSend("payments.responses", objectMapper.writeValueAsString(response));
            System.out.println("Message sent: " + responseMessage.paymentId());
        }catch (Exception e){
            System.out.println("Error caught: " + e.getMessage());
            ApiResponse<PaymentResponseMessage> response = new ApiResponse<>(false, null, e.getMessage());
            topicJmsTemplate.convertAndSend("payments.responses", objectMapper.writeValueAsString(response));
        }

    }
}
