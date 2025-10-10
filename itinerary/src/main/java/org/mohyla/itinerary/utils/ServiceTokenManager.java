package org.mohyla.itinerary.utils;

import jakarta.annotation.PostConstruct;
import org.mohyla.itinerary.dto.ApiResponse;
import org.mohyla.itinerary.dto.TokenCreateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Service
public class ServiceTokenManager {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;

    private volatile String serviceToken;

    public ServiceTokenManager(@Value("${ITINERARY_MAIN_SECRET}") String clientSecret) {
        this.webClient = WebClient.create("http://auth:8080/api");
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
            ApiResponse<String> response = webClient.post().uri("/jwt").contentType(MediaType.APPLICATION_JSON).bodyValue(request).retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("Auth service error: " + body))
                    )
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<String>>() {})
                    .retryWhen(
                            Retry.backoff(3, Duration.ofSeconds(1))
                                    .maxBackoff(Duration.ofSeconds(5))
                                    .filter(throwable ->
                                            throwable instanceof WebClientResponseException wex &&
                                                    wex.getStatusCode().is5xxServerError()
                                    )
                                    .doBeforeRetry(retrySignal ->
                                            System.out.println("Retrying auth call (" +
                                                    (retrySignal.totalRetries() + 1) + ") due to: " + retrySignal.failure().getMessage()))
                    )
                    .block();
            assert response != null;
            this.serviceToken = response.data();
            System.out.println("Token on startup and refresh " + serviceToken);
        }catch (RuntimeException e){
            System.out.println("Token wasnt created");
        }
    }

    public String getToken() {
        return serviceToken;
    }
}
