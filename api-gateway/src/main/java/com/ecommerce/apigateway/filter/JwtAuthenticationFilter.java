package com.ecommerce.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final String SECRET_KEY = "thisisaverysecretkeyforjwttestingonly1234567890";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());


    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header");
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);


            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Authorization header format");
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix

            try {
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            } catch (Exception e) {

                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired JWT token");
            }


            return chain.filter(exchange);
        };
    }

    public static class Config {

    }
}