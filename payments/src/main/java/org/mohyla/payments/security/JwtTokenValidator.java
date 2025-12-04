package org.mohyla.payments.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.mohyla.payments.exception.InvalidTokenException;
import org.mohyla.payments.exception.InvalidUserIdException;
import org.mohyla.payments.exception.TokenExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Slf4j
@Component
public class JwtTokenValidator {

    @Value("${JWT_SECRET}")
    private String secret;

    public Claims validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secret.getBytes());
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return claims.getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT token expired: {}", e.getMessage());
            throw new TokenExpiredException("Token expired", e);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token", e);
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        Object userId = claims.get("userId");
        if (userId instanceof Number number) {
            return number.longValue();
        }
        throw new InvalidUserIdException("Invalid userId in token");
    }

    public String getUsernameFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getSubject();
    }
}

