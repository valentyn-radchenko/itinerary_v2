package org.mohyla.itinerary.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ServiceTokenManager {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;

    private volatile String serviceToken;

    public ServiceTokenManager(@Value("${ITINERARY_MAIN_SECRET}") String clientSecret) {
        this.webClient = WebClient.create("http://auth-service:8080/");
        this.clientId = "itinerary-main";
        this.clientSecret = clientSecret;
    }

    @PostConstruct
    public void init() {
        refreshToken();
    }

    public void refreshToken() {
        this.serviceToken = webClient.post()
                .uri("/auth/jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "clientId", clientId,
                        "clientSecret", clientSecret
                ))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getToken() {
        return serviceToken;
    }
}
