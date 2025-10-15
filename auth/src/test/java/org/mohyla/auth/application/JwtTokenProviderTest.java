package org.mohyla.auth.application;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private final String validSecret = "mySecretKey123456789012345678901234567890";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", validSecret);
    }

    @Test
    void testGenerateServiceToken_Success() {
        String subject = "test-service";
        
        String token = jwtTokenProvider.generateServiceToken(subject);
        
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void testGenerateServiceToken_InvalidSecret() {
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", "");
        
        assertThatThrownBy(() -> jwtTokenProvider.generateServiceToken("test-service"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void testGenerateServiceToken_InvalidSubject() {
        assertThatThrownBy(() -> jwtTokenProvider.generateServiceToken(""))
                .isInstanceOf(JwtException.class);
    }
}
