package com.sgyj.popupmoah.domain.community.service;

import com.sgyj.popupmoah.domain.community.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 토큰 관리 서비스
 */
@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret:defaultSecretKeyForDevelopmentOnly}")
    private String secret;

    @Value("${jwt.access-token-expiration:3600000}") // 1시간
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:2592000000}") // 30일
    private long refreshTokenExpiration;

    /**
     * Access Token 생성
     */
    public String generateAccessToken(Member member) {
        return generateToken(member, accessTokenExpiration);
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(Member member) {
        return generateToken(member, refreshTokenExpiration);
    }

    /**
     * 토큰 생성
     */
    private String generateToken(Member member, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", member.getId());
        claims.put("username", member.getUsername());
        claims.put("role", member.getRole().name());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 사용자명 추출
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 토큰에서 회원 ID 추출
     */
    public Long extractMemberId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("memberId", Long.class);
    }

    /**
     * 토큰에서 권한 추출
     */
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * 토큰 만료일 추출
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 토큰에서 특정 클레임 추출
     */
    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 토큰에서 모든 클레임 추출
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.warn("토큰 만료 확인 중 오류 발생: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 서명 키 생성
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 토큰 만료 시간 반환 (초 단위)
     */
    public long getAccessTokenExpirationInSeconds() {
        return accessTokenExpiration / 1000;
    }
}
