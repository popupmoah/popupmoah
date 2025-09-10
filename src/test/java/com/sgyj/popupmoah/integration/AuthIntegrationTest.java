package com.sgyj.popupmoah.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgyj.popupmoah.infra.security.dto.LoginRequest;
import com.sgyj.popupmoah.infra.security.dto.LoginResponse;
import com.sgyj.popupmoah.infra.security.dto.TokenRefreshRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }
    
    @Test
    @DisplayName("로그인 후 JWT 토큰으로 인증된 요청 테스트")
    void testLoginAndAuthenticatedRequest() throws Exception {
        // Given - 로그인 요청
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        
        // When - 로그인 시도
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        LoginResponse authResponse = objectMapper.readValue(loginResponse, LoginResponse.class);
        String accessToken = authResponse.getAccessToken();
        
        // Then - 토큰이 생성되었는지 확인
        assertThat(accessToken).isNotNull();
        
        // When - 인증된 요청 (예약 목록 조회)
        mockMvc.perform(get("/api/reservations")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
        
        // When - 토큰 유효성 검증
        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("리프레시 토큰으로 새로운 액세스 토큰 발급 테스트")
    void testRefreshToken() throws Exception {
        // Given - 로그인하여 리프레시 토큰 획득
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        LoginResponse authResponse = objectMapper.readValue(loginResponse, LoginResponse.class);
        String refreshToken = authResponse.getRefreshToken();
        
        // Given - 리프레시 토큰 요청
        TokenRefreshRequest refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken(refreshToken);
        
        // When - 리프레시 토큰으로 새로운 액세스 토큰 발급
        String refreshResponse = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").value(refreshToken))
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        LoginResponse newAuthResponse = objectMapper.readValue(refreshResponse, LoginResponse.class);
        String newAccessToken = newAuthResponse.getAccessToken();
        
        // Then - 새로운 액세스 토큰이 발급되었는지 확인
        assertThat(newAccessToken).isNotNull();
        assertThat(newAccessToken).isNotEqualTo(authResponse.getAccessToken());
        
        // When - 새로운 토큰으로 인증된 요청
        mockMvc.perform(get("/api/reservations")
                        .header("Authorization", "Bearer " + newAccessToken))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("로그아웃 테스트")
    void testLogout() throws Exception {
        // Given - 로그인하여 토큰 획득
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        LoginResponse authResponse = objectMapper.readValue(loginResponse, LoginResponse.class);
        String accessToken = authResponse.getAccessToken();
        
        // When - 로그아웃
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
        
        // Then - 로그아웃 후에도 토큰은 여전히 유효 (JWT는 stateless)
        // 실제 구현에서는 블랙리스트 기능을 추가할 수 있음
        mockMvc.perform(get("/api/reservations")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("인증되지 않은 요청 시 401 에러 테스트")
    void testUnauthorizedRequest() throws Exception {
        // When - 인증 토큰 없이 보호된 엔드포인트 접근
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }
    
    @Test
    @DisplayName("유효하지 않은 토큰으로 요청 시 401 에러 테스트")
    void testInvalidTokenRequest() throws Exception {
        // When - 유효하지 않은 토큰으로 보호된 엔드포인트 접근
        mockMvc.perform(get("/api/reservations")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("권한이 없는 사용자가 관리자 엔드포인트 접근 시 403 에러 테스트")
    void testForbiddenRequest() throws Exception {
        // Given - 일반 사용자로 로그인
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password123");
        
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        LoginResponse authResponse = objectMapper.readValue(loginResponse, LoginResponse.class);
        String accessToken = authResponse.getAccessToken();
        
        // When - 일반 사용자가 관리자 엔드포인트 접근
        mockMvc.perform(get("/api/admin/popupstores")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."));
    }
    
    @Test
    @DisplayName("공개 엔드포인트는 인증 없이 접근 가능 테스트")
    void testPublicEndpointAccess() throws Exception {
        // When - 인증 없이 공개 엔드포인트 접근
        mockMvc.perform(get("/api/popupstores/active"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/popupstores/search"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/community/posts"))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("CORS 설정 테스트")
    void testCorsConfiguration() throws Exception {
        // When - CORS preflight 요청
        mockMvc.perform(options("/api/popupstores/active")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().exists("Access-Control-Allow-Headers"));
    }
}
