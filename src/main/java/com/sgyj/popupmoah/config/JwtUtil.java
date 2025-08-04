package com.sgyj.popupmoah.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 생성/파싱/검증 유틸리티.
 */
@Component
public class JwtUtil {
    private final String SECRET_KEY;
    private final long EXPIRATION = 1000 * 60 * 60; // 1시간
    private final Key key;

    public JwtUtil(@Value("${test.jwt.secret-key:test-jwt-secret-key-for-testing-purposes-12345678901234567890}") String testSecretKey) {
        // 테스트 환경에서는 설정 파일의 값을 우선 사용, 그 외에는 환경 변수 사용
        String secretKey = testSecretKey != null && !testSecretKey.isEmpty() ? testSecretKey : System.getenv("JWT_SECRET_KEY");
        
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET_KEY environment variable or test.jwt.secret-key property is not set or is empty.");
        }
        
        this.SECRET_KEY = secretKey;
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
    /**
     * username을 subject로 JWT 토큰을 생성한다.
     * @param username 사용자명
     * @return JWT 토큰
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰에서 username(subject)을 추출한다.
     * @param token JWT 토큰
     * @return 사용자명
     */
    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * JWT 토큰의 유효성을 검증한다.
     * @param token JWT 토큰
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
} 