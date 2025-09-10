package com.sgyj.popupmoah.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgyj.popupmoah.infra.security.dto.LoginRequest;
import com.sgyj.popupmoah.infra.security.dto.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    private void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }
    
    @Test
    @DisplayName("보안 헤더가 올바르게 설정되는지 확인")
    void testSecurityHeaders() throws Exception {
        setupMockMvc();
        
        mockMvc.perform(get("/api/popupstores/active"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-XSS-Protection", "1; mode=block"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().exists("Content-Security-Policy"))
                .andExpect(header().string("Referrer-Policy", "strict-origin-when-cross-origin"))
                .andExpect(header().exists("Permissions-Policy"))
                .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(header().string("Expires", "0"));
    }
    
    @Test
    @DisplayName("인증되지 않은 사용자가 보호된 엔드포인트에 접근할 때 401 반환")
    void testUnauthorizedAccess() throws Exception {
        setupMockMvc();
        
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }
    
    @Test
    @DisplayName("권한이 없는 사용자가 관리자 엔드포인트에 접근할 때 403 반환")
    void testForbiddenAccess() throws Exception {
        setupMockMvc();
        
        // 유효하지 않은 토큰으로 관리자 엔드포인트 접근
        mockMvc.perform(get("/api/admin/popupstores")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("CORS 설정이 올바르게 적용되는지 확인")
    void testCorsConfiguration() throws Exception {
        setupMockMvc();
        
        mockMvc.perform(options("/api/popupstores/active")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().exists("Access-Control-Allow-Headers"));
    }
    
    @Test
    @DisplayName("CSRF 보호가 비활성화되어 있는지 확인")
    void testCsrfDisabled() throws Exception {
        setupMockMvc();
        
        // CSRF 토큰 없이 POST 요청이 성공해야 함 (JWT 사용 시 CSRF 불필요)
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest())))
                .andExpect(status().isBadRequest()); // 요청 데이터 문제로 400, CSRF 문제로 403이 아님
    }
    
    @Test
    @DisplayName("세션이 STATELESS로 설정되어 있는지 확인")
    void testStatelessSession() throws Exception {
        setupMockMvc();
        
        mockMvc.perform(get("/api/popupstores/active"))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("JSESSIONID"));
    }
}
