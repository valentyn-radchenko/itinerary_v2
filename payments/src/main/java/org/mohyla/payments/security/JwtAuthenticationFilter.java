package org.mohyla.payments.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.mohyla.payments.exception.InvalidTokenException;
import org.mohyla.payments.exception.InvalidUserIdException;
import org.mohyla.payments.exception.TokenExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenValidator jwtTokenValidator;

    public JwtAuthenticationFilter(JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                authenticateToken(token, request);
            }
        } catch (RuntimeException e) {
            log.error("Unexpected error processing JWT filter: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }

    private void authenticateToken(String token, HttpServletRequest request) {
        try {
            Long userId = jwtTokenValidator.getUserIdFromToken(token);
            String username = jwtTokenValidator.getUsernameFromToken(token);
            
            UserPrincipal userPrincipal = new UserPrincipal(userId, username);
            
            UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                            userPrincipal, 
                            null, 
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );
            
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.debug("Authenticated user: userId={}, username={}", userId, username);
        } catch (TokenExpiredException e) {
            log.error("JWT token expired: {}", e.getMessage());
        } catch (InvalidTokenException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (InvalidUserIdException e) {
            log.error("Invalid user ID in token: {}", e.getMessage());
        }
    }
}

