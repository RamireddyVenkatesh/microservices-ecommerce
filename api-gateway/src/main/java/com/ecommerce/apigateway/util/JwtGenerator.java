package com.ecommerce.apigateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JwtGenerator {

    private static final String SECRET_KEY = "thisisaverysecretkeyforjwttestingonly1234567890";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public static String generateTestToken(String subject, long validityDays) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillis = nowMillis + TimeUnit.DAYS.toMillis(validityDays);
        Date expiration = new Date(expMillis);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .claim("roles", "ADMIN")
                .signWith(KEY)
                .compact();
    }

    public static void main(String[] args) {
        // Generate a token valid for 365 days
        String token = generateTestToken("admin@ecommerce.com", 365);

        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.println("YOUR NEW GENERATED JWT TOKEN : ");
        System.out.println(token);
        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }
}