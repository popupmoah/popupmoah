package com.sgyj.popupmoah.infra.security;

import com.sgyj.popupmoah.infra.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    
    private final JwtProperties jwtProperties;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }
    
    /**
     * JWT 토큰을 생성합니다.
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getUsername(), userDetails.getAuthorities().toString());
    }
    
    /**
     * 사용자 정보로 JWT 토큰을 생성합니다.
     */
    public String generateToken(String username, String authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorities);
        return createToken(claims, username, jwtProperties.getExpiration());
    }
    
    /**
     * 리프레시 토큰을 생성합니다.
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, username, jwtProperties.getRefreshExpiration());
    }
    
    /**
     * 토큰을 생성합니다.
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * 토큰에서 사용자명을 추출합니다.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    /**
     * 토큰에서 권한 정보를 추출합니다.
     */
    public String getAuthoritiesFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("authorities", String.class));
    }
    
    /**
     * 토큰에서 만료일을 추출합니다.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * 토큰에서 특정 클레임을 추출합니다.
     */
    public <T> T getClaimFromToken(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.resolve(claims);
    }
    
    /**
     * 토큰에서 모든 클레임을 추출합니다.
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 토큰이 만료되었는지 확인합니다.
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * 토큰의 유효성을 검증합니다.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 토큰의 유효성을 검증합니다.
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 토큰이 리프레시 토큰인지 확인합니다.
     */
    public Boolean isRefreshToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return "refresh".equals(claims.get("type"));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("리프레시 토큰 확인 실패: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * ClaimsResolver 함수형 인터페이스
     */
    @FunctionalInterface
    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }
}
