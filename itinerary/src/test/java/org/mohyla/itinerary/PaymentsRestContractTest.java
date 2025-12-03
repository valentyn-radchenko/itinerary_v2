package org.mohyla.itinerary;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(
        ids = "org.mohyla:payments:+:stubs:9095",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@ActiveProfiles("test")
public class PaymentsRestContractTest {

    @Test
    public void shouldGetAllPayments() {
        WebClient webClient = WebClient.create("http://localhost:9095");

        JsonNode payments = webClient.get()
                .uri("/payments")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        assertThat(payments).isNotNull();
        assertThat(payments.isArray()).isTrue();
        assertThat(payments.size()).isGreaterThan(0);
        
        JsonNode firstPayment = payments.get(0);
        assertThat(firstPayment.get("status").asText()).isEqualTo("COMPLETED");
        assertThat(firstPayment.has("id")).isTrue();
        assertThat(firstPayment.has("userId")).isTrue();
        assertThat(firstPayment.has("amount")).isTrue();
    }
}

