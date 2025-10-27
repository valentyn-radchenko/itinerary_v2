package org.mohyla.auth;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mohyla.auth.application.JwtTokenProvider;
import org.mohyla.auth.application.dto.ApiResponse;
import org.mohyla.auth.application.dto.TokenCreateRequest;
import org.mohyla.auth.application.utils.ClientCredentialsValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthApi {

    private final JwtTokenProvider tokenProvider;
    private final JmsTemplate queueJmsTemplate;
    private final JmsTemplate topicJmsTemplate;
    private final ClientCredentialsValidator clientCredentialsValidator;
    private final ObjectMapper objectMapper;
    public AuthApi(JwtTokenProvider provider, @Qualifier("queueJmsTemplate") JmsTemplate queueJmsTemplate, @Qualifier("topicJmsTemplate") JmsTemplate topicJmsTemplate, ClientCredentialsValidator clientCredentialsValidator, ObjectMapper objectMapper) {
        this.tokenProvider = provider;

        this.queueJmsTemplate = queueJmsTemplate;
        this.objectMapper = objectMapper;

        this.topicJmsTemplate = topicJmsTemplate;

        this.clientCredentialsValidator = clientCredentialsValidator;
    }

    @JmsListener(destination = "auth.jwt.requests")
    public void jwtRequestListener(String message) throws JsonProcessingException {
        TokenCreateRequest request = objectMapper.readValue(message, TokenCreateRequest.class);
        System.out.println("Request received: " + request.clientId() + ", " + request.clientSecret());
        if(request.clientSecret() == null){
            queueJmsTemplate.convertAndSend("auth.jwt.responses." + request.clientId(), objectMapper.writeValueAsString(new ApiResponse<String>(false,
                    null,  "No client secret provided")));
            System.out.println("Client secret not provided");
            return;
        }
        if(!clientCredentialsValidator.validate(request.clientId(), request.clientSecret())){
            queueJmsTemplate.convertAndSend("auth.jwt.responses." + request.clientId(), objectMapper.writeValueAsString(new ApiResponse<String>(false,
                    null,  "False credentials to get a token provided")));
            System.out.println("Client secret was not validated");
            return;
        }
        try {
            String token = tokenProvider.generateServiceToken(request.clientId());
            System.out.println("Token generated: " + token);
            if(token != null && !token.isEmpty()){
                ApiResponse<String> response = new ApiResponse<>(true, token, null);
                queueJmsTemplate.convertAndSend("auth.jwt.responses." + request.clientId(),objectMapper.writeValueAsString(response));
                System.out.println("Token sent: " + token);
                System.out.println("Queue name: " + "auth.jwt.responses." + request.clientId());
                return;
            }
            ApiResponse<String> response = new ApiResponse<>(false, null, "Invalid token was created");
            queueJmsTemplate.convertAndSend("auth.jwt.responses." + request.clientId(), objectMapper.writeValueAsString(response));
            System.out.println(response.error());

        } catch (Exception e) {
            queueJmsTemplate.convertAndSend("auth.jwt.responses." + request.clientId(),
                  objectMapper.writeValueAsString(
                          new ApiResponse<String>(false, null, e.getMessage())));
            System.out.println("Exception creating a token: " + e.getMessage());
        }
    }

}
