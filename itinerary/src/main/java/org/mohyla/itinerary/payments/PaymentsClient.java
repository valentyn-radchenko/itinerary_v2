package org.mohyla.itinerary.payments;

import org.mohyla.itinerary.dto.ApiResponse;
import org.mohyla.itinerary.dto.PaymentRequestMessage;
import org.mohyla.itinerary.dto.PaymentResponseMessage;
import org.mohyla.itinerary.utils.ServiceTokenManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class PaymentsClient {

    private final WebClient webClient;
    private final ServiceTokenManager tokenManager;

    public PaymentsClient(ServiceTokenManager tokenManager) {
        this.tokenManager = tokenManager;
        this.webClient = WebClient.create("http://payments:8080/api");
    }


    public PaymentResponseMessage createPayment(PaymentRequestMessage message){
        String token = tokenManager.getToken();
        System.out.println("Token in main app payment client " + token);
        try {
            ApiResponse<PaymentResponseMessage> response = webClient.post()
                    .uri("/")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(message)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("Payment service error: " + body))
                    )
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<PaymentResponseMessage>>() {})
                    .retryWhen(
                            Retry.backoff(3, Duration.ofSeconds(1))
                                    .maxBackoff(Duration.ofSeconds(5))
                                    .filter(throwable ->
                                            throwable instanceof WebClientResponseException wex &&
                                                    wex.getStatusCode().is5xxServerError()
                                    )
                                    .doBeforeRetry(retrySignal ->
                                            System.out.println("Retrying payments call (" +
                                                    (retrySignal.totalRetries() + 1) + ") due to: " + retrySignal.failure().getMessage()))
                    )
                    .block();
            assert response != null;
            return response.data();
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to call payment service: " + e.getMessage(), e);
        }
    }

}
