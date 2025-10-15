package org.mohyla.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mohyla.auth.application.JwtTokenProvider;
import org.mohyla.auth.application.dto.TokenCreateRequest;
import org.mohyla.auth.application.utils.ClientCredentialsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthApi.class)
class AuthApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private ClientCredentialsValidator credentialValidator;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateToken_Success() throws Exception {
        TokenCreateRequest request = new TokenCreateRequest("test-client", "test-secret");
        String expectedToken = "test-jwt-token";
        
        when(credentialValidator.validate(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateServiceToken(anyString())).thenReturn(expectedToken);
        
        mockMvc.perform(post("/api/jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(expectedToken));
    }

    @Test
    void testCreateToken_InvalidCredentials() throws Exception {
        TokenCreateRequest request = new TokenCreateRequest("invalid-client", "invalid-secret");
        
        when(credentialValidator.validate(anyString(), anyString())).thenReturn(false);
        
        mockMvc.perform(post("/api/jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }
}
