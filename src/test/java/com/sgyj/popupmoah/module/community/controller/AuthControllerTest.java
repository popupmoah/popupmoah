package com.sgyj.popupmoah.module.community.controller;

import com.sgyj.popupmoah.config.JwtUtil;
import com.sgyj.popupmoah.module.community.entity.Member;
import com.sgyj.popupmoah.module.community.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtUtil jwtUtil;

    // 시큐리티 우회용 MockUser 적용
    @BeforeEach
    void setup() {
        // 필요한 경우 테스트 데이터 자동 주입
    }

    // 테스트 환경용 JwtUtil Bean 등록
    @TestConfiguration
    static class JwtUtilTestConfig {
        @Bean
        public JwtUtil jwtUtil() {
            return new JwtUtil("testtesttesttesttesttesttesttest12");
        }
    }
    @TestConfiguration
    static class SecurityConfig {
        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring().requestMatchers("/**");
        }
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                .param("username", "testuser")
                .param("password", "testpass"))
                .andExpect(status().isOk())
                .andExpect(content().string("회원가입 성공"));
        assertThat(memberRepository.findByUsername("testuser")).isPresent();
    }

    @Test
    @DisplayName("회원가입 실패 - 중복")
    void signup_fail_duplicate() throws Exception {
        memberRepository.save(Member.builder().username("dupuser").password("pw").build());
        mockMvc.perform(post("/api/auth/signup")
                .param("username", "dupuser")
                .param("password", "pw2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이미 존재하는 사용자입니다."));
    }

    @Test
    @DisplayName("로그인 성공 - JWT 발급")
    void login_success() throws Exception {
        String username = "jwtuser";
        String password = "jwtpass";
        String encoded = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(password);
        memberRepository.save(Member.builder().username(username).password(encoded).build());
        String response = mockMvc.perform(post("/api/auth/login")
                .param("username", username)
                .param("password", password))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(jwtUtil.validateToken(response)).isTrue();
        assertThat(jwtUtil.getUsername(response)).isEqualTo(username);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_wrong_password() throws Exception {
        String username = "failuser";
        String password = "rightpass";
        String encoded = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(password);
        memberRepository.save(Member.builder().username(username).password(encoded).build());
        mockMvc.perform(post("/api/auth/login")
                .param("username", username)
                .param("password", "wrongpass"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("아이디 또는 비밀번호가 올바르지 않습니다."));
    }
} 