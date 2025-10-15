package org.mohyla.itinerary.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.mohyla.itinerary.dto.ApiResponse;
import org.mohyla.itinerary.dto.TokenCreateRequest;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
public class ServiceTokenManager {

    private final JmsTemplate jmsTemplate;
    private final String clientId;
    private final String clientSecret;
    private final ObjectMapper objectMapper;

    private volatile String serviceToken;

    public ServiceTokenManager(JmsTemplate jmsTemplate, @Value("${ITINERARY_MAIN_SECRET}") String clientSecret, ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
        this.jmsTemplate.setPubSubDomain(false);
        this.clientId = "itinerary-main";
        this.clientSecret = clientSecret;
    }

    @PostConstruct
    public void init() {
        refreshToken();
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void refreshToken() {
        TokenCreateRequest request = new TokenCreateRequest(clientId, clientSecret);
        try {
            String json = objectMapper.writeValueAsString(request);
            jmsTemplate.convertAndSend("auth.jwt.requests", json);
            System.out.println("Sent token request via JMS");
        }catch (RuntimeException | JsonProcessingException e){
            System.out.println("Token request failed: " + e.getMessage());
        }
    }

    @JmsListener(destination = "auth.jwt.responses.itinerary-main",
            containerFactory = "queueListenerFactory"
    )
    public void receiveToken(String message) throws JsonProcessingException {
        System.out.println("Message from auth: " + message);
        ApiResponse<String> tokenResponse = objectMapper.readValue(message, new TypeReference<ApiResponse<String>>(){});
        if(tokenResponse.success()){
            serviceToken = tokenResponse.data();
            System.out.println("Token received successfully");
            System.out.println("Token: " + serviceToken);
        }else{
            System.out.println("Token was not received");
            System.out.println(tokenResponse.error());
        }
    }

    public String getToken() {
        return serviceToken;
    }
}
