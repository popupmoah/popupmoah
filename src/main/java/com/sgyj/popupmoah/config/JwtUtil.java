package com.sgyj.popupmoah.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 생성/파싱/검증 유틸리티.
 */
@Component
public class JwtUtil {
    private final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");
    private final long EXPIRATION = 1000 * 60 * 60; // 1시간
    private final Key key;

    public JwtUtil() {
        if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET_KEY environment variable is not set or is empty.");
        }
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