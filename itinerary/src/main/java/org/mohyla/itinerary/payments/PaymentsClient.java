package org.mohyla.itinerary.payments;

import org.mohyla.itinerary.payments.dto.PaymentRequestMessage;
import org.mohyla.itinerary.payments.dto.PaymentResponseMessage;
import org.mohyla.itinerary.utils.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PaymentsClient {

    private final WebClient webClient;
    private final JwtTokenProvider jwtTokenProvider;

    public PaymentsClient(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.webClient = WebClient.create("http://payments:8080/api");
    }


    public PaymentResponseMessage createPayment(PaymentRequestMessage message){
        String token = jwtTokenProvider.generateServiceToken();

        return webClient.post().uri("/").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).bodyValue(message).retrieve().bodyToMono(PaymentResponseMessage.class).block();

    }
}
