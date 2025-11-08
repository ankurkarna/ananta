package org.karna.ankur.ananta.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {
    private final Key signingKey;
    private final long jwtExpirationMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-ms}") long jwtExpirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationMs = jwtExpirationMs;
        log.info("JWT Service initialized with expiration: {} ms", jwtExpirationMs);
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
        log.debug("Generated JWT token for user: {}, expires at: {}", username, exp);
        return token;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String sub = extractUsername(token);
        boolean isValid = (sub.equals(userDetails.getUsername()) && !isTokenExpired(token));
        log.debug("Token validation for user {}: {}", userDetails.getUsername(), isValid);
        return isValid;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        Date exp = extractExpiration(token);
        boolean expired = exp.before(new Date());
        if (expired) {
            log.warn("Token is expired. Expiration date: {}, Current date: {}", exp, new Date());
        }
        return expired;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}
