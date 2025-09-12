package com.virtualgarage.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Service for JWT token generation, validation, and management.
 * 
 * Features:
 * - Access token generation with short expiration
 * - Refresh token generation with longer expiration
 * - Token validation and parsing
 * - Token blacklisting with Redis
 * - User information extraction from tokens
 */
@Slf4j
@Service
public class JwtService {

    private final String secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final RedisTemplate<String, String> redisTemplate;
    private final SecretKey key;

    public JwtService(
            @Value("${spring.security.jwt.secret}") String secretKey,
            @Value("${spring.security.jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${spring.security.jwt.refresh-token-expiration}") long refreshTokenExpiration,
            RedisTemplate<String, String> redisTemplate) {
        this.secretKey = secretKey;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.redisTemplate = redisTemplate;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Generate access token for authenticated user
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("type", "access");
        return generateToken(extraClaims, userDetails, accessTokenExpiration);
    }

    /**
     * Generate refresh token for token renewal
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("type", "refresh");
        return generateToken(extraClaims, userDetails, refreshTokenExpiration);
    }

    /**
     * Generate token with custom claims and expiration
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration, ChronoUnit.MILLIS)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract token type (access/refresh)
     */
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    /**
     * Validate token against user details
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) 
                   && !isTokenExpired(token) 
                   && !isTokenBlacklisted(token);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Check if token is blacklisted in Redis
     */
    public boolean isTokenBlacklisted(String token) {
        String key = "blacklisted_token:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Blacklist token (for logout functionality)
     */
    public void blacklistToken(String token) {
        try {
            String key = "blacklisted_token:" + token;
            Date expiration = extractExpiration(token);
            long ttl = expiration.getTime() - System.currentTimeMillis();
            
            if (ttl > 0) {
                redisTemplate.opsForValue().set(key, "true", ttl, TimeUnit.MILLISECONDS);
                log.info("Token blacklisted successfully");
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
        }
    }

    /**
     * Validate refresh token
     */
    public boolean isValidRefreshToken(String token) {
        try {
            String tokenType = extractTokenType(token);
            return "refresh".equals(tokenType) && !isTokenExpired(token) && !isTokenBlacklisted(token);
        } catch (Exception e) {
            log.error("Refresh token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get token expiration time in milliseconds
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}