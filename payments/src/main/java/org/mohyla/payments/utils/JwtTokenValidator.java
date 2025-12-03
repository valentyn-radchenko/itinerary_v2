package org.mohyla.payments.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenValidator {

    @Value("${JWT_SECRET}")
    private String secret;

    public Jws<Claims> validate(String token) {
        try {
            log.debug("Validating JWT token");
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
        } catch (JwtException e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            throw new JwtException("Invalid or expired JWT: " + e.getMessage());
        }
    }
}
