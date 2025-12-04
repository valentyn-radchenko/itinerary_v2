package org.mohyla.auth.application;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.extern.slf4j.Slf4j;
import org.mohyla.auth.exception.InvalidTokenException;
import org.mohyla.auth.exception.TokenExpiredException;
import org.mohyla.auth.exception.TokenGenerationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
            throw new TokenGenerationException("JWT secret key is too weak: " + e.getMessage(), e);

        } catch (JwtException e) {
            log.error("Failed to create JWT token: {}", e.getMessage(), e);
            throw new TokenGenerationException("Failed to create JWT token: " + e.getMessage(), e);
        }
    }

    public String generateUserToken(Long userId, String username, String email) {
        try {
            if (secret == null || secret.isBlank()) {
                log.error("JWT secret is missing or empty");
                throw new JwtException("JWT secret is missing or empty");
            }

            Date now = new Date();
            Date expiry = new Date(now.getTime() + 24 * 60 * 60 * 1000); // 24 hours

            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userId);
            claims.put("username", username);
            claims.put("email", email);
            claims.put("type", "USER");

            Key key = Keys.hmacShaKeyFor(secret.getBytes());
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(expiry)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            
            log.debug("Successfully issued user JWT token for userId: {}", userId);
            return token;

        } catch (WeakKeyException e) {
            log.error("JWT secret key is too weak: {}", e.getMessage());
            throw new TokenGenerationException("JWT secret key is too weak: " + e.getMessage(), e);
        } catch (JwtException e) {
            log.error("Failed to create user JWT token: {}", e.getMessage(), e);
            throw new TokenGenerationException("Failed to create user JWT token: " + e.getMessage(), e);
        }
    }

    public Jws<Claims> validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secret.getBytes());
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            throw new TokenExpiredException("Token expired: " + e.getMessage(), e);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token: " + e.getMessage(), e);
        }
    }

}


