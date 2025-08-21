package com.example.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private Key signingKey;

    @PostConstruct
    public void init() {
        signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email) { // ĐÃ SỬA
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder().setSubject(email).setIssuedAt(now).setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256).compact();
    }

    public String generateRefreshToken(String email) { // ĐÃ SỬA
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder().setSubject(email).setIssuedAt(now).setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256).compact();
    }

    public String getEmailFromToken(String token) { // ĐÃ SỬA
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
