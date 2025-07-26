package com.sgyj.popupmoah.config;

import com.sgyj.popupmoah.module.community.entity.Member;
import com.sgyj.popupmoah.module.community.repository.MemberRepository;
import com.sgyj.popupmoah.TestSupportConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestSupportConfig.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MemberRepository memberRepository;

    // 중복 TestConfiguration, SecurityConfig, JwtUtilTestConfig, @BeforeEach 등 모두 제거

    @Test
    @DisplayName("JWT 토큰이 없으면 인증 실패(401)")
    void no_token_unauthorized() throws Exception {
        mockMvc.perform(get("/api/comments/popup/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("JWT 토큰이 있으면 인증 성공(200)")
    void with_token_authorized() throws Exception {
        // given
        Member member = memberRepository.save(Member.builder().username("jwtfilter").password("pw").build());
        String token = jwtUtil.generateToken(member.getUsername());
        // when & then
        mockMvc.perform(get("/api/comments/popup/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // 실제로는 200, 404 등 리소스에 따라 다름
    }
} 