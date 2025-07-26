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
import com.sgyj.popupmoah.TestSupportConfig;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestSupportConfig.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CategorySaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 중복 TestConfiguration, SecurityConfig, JwtUtilTestConfig, @BeforeEach 등 모두 제거

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