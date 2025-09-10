package com.sgyj.popupmoah.security;

import com.sgyj.popupmoah.infra.config.SecurityConfig;
import com.sgyj.popupmoah.infra.security.JwtAuthenticationFilter;
import com.sgyj.popupmoah.infra.security.filter.SecurityHeadersFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigurationTest {
    
    @Autowired
    private SecurityConfig securityConfig;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private SecurityHeadersFilter securityHeadersFilter;
    
    @Test
    @DisplayName("보안 설정이 올바르게 로드되는지 확인")
    void testSecurityConfigurationLoaded() {
        assertThat(securityConfig).isNotNull();
        assertThat(passwordEncoder).isNotNull();
        assertThat(jwtAuthenticationFilter).isNotNull();
        assertThat(securityHeadersFilter).isNotNull();
    }
    
    @Test
    @DisplayName("비밀번호 인코더가 올바르게 작동하는지 확인")
    void testPasswordEncoder() {
        // Given
        String rawPassword = "testPassword123!";
        
        // When
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        // Then
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
        assertThat(passwordEncoder.matches("wrongPassword", encodedPassword)).isFalse();
    }
    
    @Test
    @DisplayName("비밀번호 인코더가 다른 해시를 생성하는지 확인")
    void testPasswordEncoderUniqueness() {
        // Given
        String rawPassword = "testPassword123!";
        
        // When
        String encodedPassword1 = passwordEncoder.encode(rawPassword);
        String encodedPassword2 = passwordEncoder.encode(rawPassword);
        
        // Then
        assertThat(encodedPassword1).isNotEqualTo(encodedPassword2);
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword1)).isTrue();
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword2)).isTrue();
    }
    
    @Test
    @DisplayName("JWT 인증 필터가 올바르게 설정되는지 확인")
    void testJwtAuthenticationFilter() {
        assertThat(jwtAuthenticationFilter).isNotNull();
        assertThat(jwtAuthenticationFilter.getClass().getSimpleName()).isEqualTo("JwtAuthenticationFilter");
    }
    
    @Test
    @DisplayName("보안 헤더 필터가 올바르게 설정되는지 확인")
    void testSecurityHeadersFilter() {
        assertThat(securityHeadersFilter).isNotNull();
        assertThat(securityHeadersFilter.getClass().getSimpleName()).isEqualTo("SecurityHeadersFilter");
    }
}
