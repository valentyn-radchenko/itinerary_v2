package org.mohyla.itinerary.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mohyla.itinerary.dto.ApiResponse;
import org.mohyla.itinerary.dto.TokenCreateRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceTokenManagerTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ServiceTokenManager serviceTokenManager;

    @BeforeEach
    void setUp() {
        serviceTokenManager = new ServiceTokenManager("test-secret");
        ReflectionTestUtils.setField(serviceTokenManager, "webClient", webClient);
        ReflectionTestUtils.setField(serviceTokenManager, "clientId", "test-client");
        ReflectionTestUtils.setField(serviceTokenManager, "clientSecret", "test-secret");
    }

    @Test
    void testGetToken_ReturnsToken() {
        String expectedToken = "test-jwt-token";
        ReflectionTestUtils.setField(serviceTokenManager, "serviceToken", expectedToken);
        
        String result = serviceTokenManager.getToken();
        
        assertThat(result).isEqualTo(expectedToken);
    }

    @Test
    void testRefreshToken_Success() {
        String expectedToken = "new-jwt-token";
        ApiResponse<String> mockResponse = new ApiResponse<>(true, expectedToken, null);
        
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/jwt")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(TokenCreateRequest.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(mockResponse));
        
        serviceTokenManager.refreshToken();
        
        verify(webClient).post();
        verify(requestBodyUriSpec).uri("/jwt");
        verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
        verify(requestBodySpec).bodyValue(any(TokenCreateRequest.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(any(ParameterizedTypeReference.class));
        
        String result = serviceTokenManager.getToken();
        assertThat(result).isEqualTo(expectedToken);
    }
}
