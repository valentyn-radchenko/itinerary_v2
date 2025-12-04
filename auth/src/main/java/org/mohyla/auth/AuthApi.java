package org.mohyla.auth;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mohyla.auth.application.JwtTokenProvider;
import org.mohyla.auth.application.dto.ApiResponse;
import org.mohyla.auth.application.dto.TokenCreateRequest;
import org.mohyla.auth.application.utils.ClientCredentialsValidator;
import org.mohyla.auth.exception.TokenGenerationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthApi {

    private static final String AUTH_JWT_RESPONSES_TOPIC = "auth.jwt.responses.";

    private final JwtTokenProvider tokenProvider;
    private final JmsTemplate topicJmsTemplate;
    private final ClientCredentialsValidator clientCredentialsValidator;
    private final ObjectMapper objectMapper;
    public AuthApi(JwtTokenProvider provider, @Qualifier("topicJmsTemplate") JmsTemplate topicJmsTemplate, ClientCredentialsValidator clientCredentialsValidator, ObjectMapper objectMapper) {
        this.tokenProvider = provider;

        this.objectMapper = objectMapper;
        this.topicJmsTemplate = topicJmsTemplate;

        this.clientCredentialsValidator = clientCredentialsValidator;
    }

    @JmsListener(destination = "auth.jwt.requests")
    public void jwtRequestListener(String message) throws JsonProcessingException {
        TokenCreateRequest request = objectMapper.readValue(message, TokenCreateRequest.class);
        log.debug("JWT token request received for clientId: {}", request.clientId());
        if(request.clientSecret() == null){
            topicJmsTemplate.convertAndSend(AUTH_JWT_RESPONSES_TOPIC + request.clientId(), objectMapper.writeValueAsString(new ApiResponse<String>(false,
                    null,  "No client secret provided")));
            log.warn("Client secret not provided for clientId: {}", request.clientId());
            return;
        }
        if(!clientCredentialsValidator.validate(request.clientId(), request.clientSecret())){
            topicJmsTemplate.convertAndSend(AUTH_JWT_RESPONSES_TOPIC + request.clientId(), objectMapper.writeValueAsString(new ApiResponse<String>(false,
                    null,  "False credentials to get a token provided")));
            log.warn("Client credentials validation failed for clientId: {}", request.clientId());
            return;
        }
        try {
            String token = tokenProvider.generateServiceToken(request.clientId());
            log.debug("JWT token generated for clientId: {}", request.clientId());
            if(token != null && !token.isEmpty()){
                ApiResponse<String> response = new ApiResponse<>(true, token, null);
                topicJmsTemplate.convertAndSend(AUTH_JWT_RESPONSES_TOPIC + request.clientId(),objectMapper.writeValueAsString(response));
                log.debug("JWT token sent to topic: {}{}", AUTH_JWT_RESPONSES_TOPIC, request.clientId());
                return;
            }
            ApiResponse<String> response = new ApiResponse<>(false, null, "Invalid token was created");
            topicJmsTemplate.convertAndSend(AUTH_JWT_RESPONSES_TOPIC + request.clientId(), objectMapper.writeValueAsString(response));
            log.error("Invalid token was created for clientId: {}", request.clientId());

        } catch (TokenGenerationException e) {
            topicJmsTemplate.convertAndSend(AUTH_JWT_RESPONSES_TOPIC + request.clientId(),
                  objectMapper.writeValueAsString(
                          new ApiResponse<String>(false, null, e.getMessage())));
            log.error("Token generation failed for clientId: {}: {}", request.clientId(), e.getMessage(), e);
        } catch (RuntimeException e) {
            topicJmsTemplate.convertAndSend(AUTH_JWT_RESPONSES_TOPIC + request.clientId(),
                  objectMapper.writeValueAsString(
                          new ApiResponse<String>(false, null, "Internal error occurred")));
            log.error("Unexpected error while creating token for clientId: {}: {}", request.clientId(), e.getMessage(), e);
        }
    }

}
