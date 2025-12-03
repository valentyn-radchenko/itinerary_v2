package org.mohyla.auth.application;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${JWT_SECRET}")
    private String secret;

    public String generateServiceToken(String subject){
        try {
            if (secret == null || secret.isBlank()) {
                log.error("JWT secret is missing or empty");
                throw new JwtException("JWT secret is missing or empty");
            }
            if (subject == null || subject.isBlank()) {
                log.error("Subject is null or empty: {}", subject);
                throw new JwtException("Subject cannot be null or empty");
            }

            Date now = new Date();
            Date expiry = new Date(now.getTime() + 15 * 60 * 1000);

            Key key = Keys.hmacShaKeyFor(secret.getBytes());
            String token = Jwts.builder()
                    .setSubject(subject)
                    .setIssuedAt(now)
                    .setExpiration(expiry)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            log.debug("Successfully issued JWT token for subject: {}", subject);

            return token;

        } catch (WeakKeyException e) {
            log.error("JWT secret key is too weak: {}", e.getMessage());
            throw new JwtException("JWT secret key is too weak: " + e.getMessage(), e);

        } catch (Exception e) {
            log.error("Failed to create JWT token: {}", e.getMessage(), e);
            throw new JwtException("Failed to create JWT token: " + e.getMessage(), e);
        }
    }
//    public Jws<Claims> validateToken(String token) {
//        try {
//            return Jwts.parserBuilder()
//                    .setSigningKey(getSigningKey())
//                    .build()
//                    .parseClaimsJws(token);
//        } catch (JwtException | IllegalArgumentException e) {
//            // Signature invalid, token malformed, or expired
//            throw new RuntimeException("Invalid or expired token: " + e.getMessage());
//        }
//    }

}

