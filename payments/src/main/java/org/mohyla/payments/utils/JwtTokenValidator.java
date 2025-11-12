package org.mohyla.payments.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenValidator {

    @Value("${JWT_SECRET}")
    private String secret;

    public Jws<Claims> validate(String token) {
        try {
            System.out.println("Token in payments token validator");
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
        } catch (JwtException e) {
            System.out.println("Throwing token validation error: " + e.getMessage());
            throw new JwtException("Invalid or expired JWT: " + e.getMessage());
        }
    }
}
