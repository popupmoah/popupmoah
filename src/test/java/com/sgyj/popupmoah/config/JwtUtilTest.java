package com.sgyj.popupmoah.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    @DisplayName("JWT 토큰 생성 및 파싱, 검증")
    void generate_and_parse_and_validate_token() {
        String username = "jwtuser";
        String token = jwtUtil.generateToken(username);
        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.getUsername(token)).isEqualTo(username);
    }

    @Test
    @DisplayName("잘못된 토큰 검증 실패")
    void invalid_token() {
        String invalidToken = "invalid.jwt.token";
        assertThat(jwtUtil.validateToken(invalidToken)).isFalse();
    }
} 