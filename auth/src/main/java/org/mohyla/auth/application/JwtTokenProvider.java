package org.mohyla.auth.application;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${JWT_SECRET}")
    private String secret;

    public String generateServiceToken(String subject){
        try {
            if (secret == null || secret.isBlank()) {
                System.out.println("Secret is missing " + secret);
                throw new JwtException("JWT secret is missing or empty");
            }
            if (subject == null || subject.isBlank()) {
                System.out.println("Subject is null: " + subject);
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
            System.out.println("Issued token in auth " + token);

            return token;

        } catch (WeakKeyException e) {
            System.out.println("Token weak key error " + e.getMessage());
            throw new JwtException("JWT secret key is too weak: " + e.getMessage(), e);

        } catch (Exception e) {
            System.out.println("Token creation error " + e.getMessage());
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

