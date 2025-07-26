package com.sgyj.popupmoah.module.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CategorySaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 시큐리티 우회용 MockUser 적용
    @BeforeEach
    void setup() {
        // 필요한 경우 테스트 데이터 자동 주입
    }

    @TestConfiguration
    static class SecurityConfig {
        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring().requestMatchers("/**");
        }
    }

    @Test
    @DisplayName("카테고리 등록 성공")
    void registerCategory_success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "테스트카테고리");
        request.put("description", "설명");
        request.put("sortOrder", 1.0f);
        request.put("active", true);
        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("테스트카테고리"));
    }

    @Test
    @DisplayName("카테고리 수정 성공")
    void updateCategory_success() throws Exception {
        // 먼저 등록
        Map<String, Object> request = new HashMap<>();
        request.put("name", "카테고리");
        request.put("description", "설명");
        request.put("sortOrder", 1.0f);
        request.put("active", true);
        String response = mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).get("id").asLong();

        // 수정
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("name", "수정된카테고리");
        updateRequest.put("description", "수정설명");
        updateRequest.put("sortOrder", 2.0f);
        updateRequest.put("active", false);
        mockMvc.perform(put("/api/v1/categories/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정된카테고리"));
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategory_success() throws Exception {
        // 먼저 등록
        Map<String, Object> request = new HashMap<>();
        request.put("name", "카테고리");
        request.put("description", "설명");
        request.put("sortOrder", 1.0f);
        request.put("active", true);
        String response = mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).get("id").asLong();

        // 삭제
        mockMvc.perform(delete("/api/v1/categories/" + id))
                .andExpect(status().isNoContent());
    }
} 