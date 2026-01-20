package com.example.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenService {
    
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;
    
    /**
     * 生成 Access Token
     */
    public String generateAccessToken(UserDetails userDetails) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpiration());
        
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .claim("authorities", getAuthorities(userDetails))
            .issuedAt(new Date())
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
    }
    
    /**
     * 验证 Token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 从 Token 中提取用户名
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
    
    private List<String> getAuthorities(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    }
}
