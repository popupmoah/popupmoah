package com.sgyj.popupmoah.security;

import com.sgyj.popupmoah.infra.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class JwtTokenTest {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    private UsernamePasswordAuthenticationToken authentication;
    
    @BeforeEach
    void setUp() {
        authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
    
    @Test
    @DisplayName("JWT 토큰 생성 및 검증 테스트")
    void testJwtTokenGenerationAndValidation() {
        // Given
        String token = tokenProvider.generateToken(authentication);
        
        // When & Then
        assertThat(token).isNotNull();
        assertThat(tokenProvider.validateToken(token)).isTrue();
        assertThat(tokenProvider.getUsernameFromToken(token)).isEqualTo("test@example.com");
        assertThat(tokenProvider.getAuthoritiesFromToken(token)).contains("ROLE_USER");
    }
    
    @Test
    @DisplayName("리프레시 토큰 생성 및 검증 테스트")
    void testRefreshTokenGenerationAndValidation() {
        // Given
        String refreshToken = tokenProvider.generateRefreshToken("test@example.com");
        
        // When & Then
        assertThat(refreshToken).isNotNull();
        assertThat(tokenProvider.validateToken(refreshToken)).isTrue();
        assertThat(tokenProvider.isRefreshToken(refreshToken)).isTrue();
        assertThat(tokenProvider.getUsernameFromToken(refreshToken)).isEqualTo("test@example.com");
    }
    
    @Test
    @DisplayName("유효하지 않은 토큰 검증 테스트")
    void testInvalidTokenValidation() {
        // Given
        String invalidToken = "invalid.token.here";
        
        // When & Then
        assertThat(tokenProvider.validateToken(invalidToken)).isFalse();
    }
    
    @Test
    @DisplayName("토큰에서 사용자명 추출 테스트")
    void testUsernameExtraction() {
        // Given
        String token = tokenProvider.generateToken(authentication);
        
        // When
        String username = tokenProvider.getUsernameFromToken(token);
        
        // Then
        assertThat(username).isEqualTo("test@example.com");
    }
    
    @Test
    @DisplayName("토큰에서 권한 추출 테스트")
    void testAuthoritiesExtraction() {
        // Given
        String token = tokenProvider.generateToken(authentication);
        
        // When
        String authorities = tokenProvider.getAuthoritiesFromToken(token);
        
        // Then
        assertThat(authorities).contains("ROLE_USER");
    }
    
    @Test
    @DisplayName("토큰 만료 시간 확인 테스트")
    void testTokenExpiration() {
        // Given
        String token = tokenProvider.generateToken(authentication);
        
        // When
        long expirationTime = tokenProvider.getExpirationDateFromToken(token).getTime();
        long currentTime = System.currentTimeMillis();
        
        // Then
        assertThat(expirationTime).isGreaterThan(currentTime);
        assertThat(tokenProvider.isTokenExpired(token)).isFalse();
    }
}


