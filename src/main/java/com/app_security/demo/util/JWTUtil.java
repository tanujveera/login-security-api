package com.app_security.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JWTUtil {

    // Generate a secure key for HS512. In production, store and retrieve this key securely.
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // In production, store this secret securely (e.g., in environment variables)
//    private final String SECRET_KEY = "YourSecretKeyForJWTGeneration";

    // Token validity in milliseconds (e.g., 1 hour)
    private final long JWT_TOKEN_VALIDITY = 60 * 60 * 1000;

    public JWTUtil() {
        System.out.println(" Secret Key: "+secretKey.getAlgorithm()+" "+secretKey.getEncoded()+" "+secretKey.getFormat());
    }

    // Generate token for user
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_TOKEN_VALIDITY);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey,SignatureAlgorithm.HS512)
                .compact();
    }

    // Validate token
    public boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Check if the token has expired
    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // Generic method to extract claims
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}

