package org.mohyla.itinerary.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.mohyla.itinerary.dto.ApiResponse;
import org.mohyla.itinerary.dto.TokenCreateRequest;
import org.mohyla.itinerary.exception.ServiceCommunicationException;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ServiceTokenManager {

    private final JmsTemplate jmsTemplate;
    private final String clientId;
    private final String clientSecret;
    private final ObjectMapper objectMapper;

    private String serviceToken;

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
            log.info("Sent service token refresh request to auth service");
        }catch (JsonProcessingException e){
            log.error("Failed to serialize token refresh request: {}", e.getMessage(), e);
            throw new ServiceCommunicationException("Failed to send token refresh request", e);
        }catch (RuntimeException e){
            log.error("Failed to send token refresh request: {}", e.getMessage(), e);
            throw new ServiceCommunicationException("Failed to send token refresh request", e);
        }
    }

    @JmsListener(destination = "auth.jwt.responses.itinerary-main",
            containerFactory = "topicListenerFactory"
    )
    public void receiveToken(String message) throws JsonProcessingException {
        log.debug("Received token response from auth service");
        ApiResponse<String> tokenResponse = objectMapper.readValue(message, new TypeReference<ApiResponse<String>>(){});
        if(tokenResponse.success()){
            serviceToken = tokenResponse.data();
            log.info("Service token refreshed successfully");
        }else{
            log.error("Failed to receive token: {}", tokenResponse.error());
        }
    }

    public String getToken() {
        return serviceToken;
    }
}
