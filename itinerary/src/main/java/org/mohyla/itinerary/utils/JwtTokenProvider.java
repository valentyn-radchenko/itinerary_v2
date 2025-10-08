package org.mohyla.itinerary.utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${JWT_SECRET}")
    private String secret;

    public String generateServiceToken(){
        Date now = new Date();

        Date expiry = new Date(now.getTime() + 5 * 60 * 1000);

        return Jwts.builder().setSubject("itinerary-main").setIssuedAt(now).setExpiration(expiry).addClaims(Map.of("role", "internal-service"))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256).compact();
    }
}
