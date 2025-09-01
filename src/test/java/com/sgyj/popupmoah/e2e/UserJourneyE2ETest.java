package com.sgyj.popupmoah.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgyj.popupmoah.infra.security.dto.LoginRequest;
import com.sgyj.popupmoah.infra.security.dto.LoginResponse;
import com.sgyj.popupmoah.popupstore.application.dto.PopupStoreCreateRequest;
import com.sgyj.popupmoah.popupstore.application.dto.PopupStoreResponse;
import com.sgyj.popupmoah.popupstore.application.dto.PopupStoreSearchRequest;
import com.sgyj.popupmoah.reservation.application.dto.ReservationCreateRequest;
import com.sgyj.popupmoah.reservation.application.dto.ReservationResponse;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class UserJourneyE2ETest {
    
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
    @DisplayName("사용자 여행: 팝업스토어 검색 → 조회 → 예약 → 관리")
    void testCompleteUserJourney() throws Exception {
        // ===== 1단계: 팝업스토어 검색 =====
        PopupStoreSearchRequest searchRequest = PopupStoreSearchRequest.builder()
                .keyword("패션")
                .category("패션")
                .location("서울")
                .page(0)
                .size(10)
                .sortBy("createdAt")
                .sortDirection("desc")
                .build();
        
        String searchResponse = mockMvc.perform(get("/api/popupstores/search")
                        .param("keyword", "패션")
                        .param("category", "패션")
                        .param("location", "서울")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.popupStores").isArray())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // ===== 2단계: 활성 팝업스토어 목록 조회 =====
        String activeStoresResponse = mockMvc.perform(get("/api/popupstores/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // ===== 3단계: 관리자 로그인 =====
        LoginRequest adminLoginRequest = new LoginRequest();
        adminLoginRequest.setEmail("admin@example.com");
        adminLoginRequest.setPassword("admin123");
        
        String adminLoginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        LoginResponse adminAuth = objectMapper.readValue(adminLoginResponse, LoginResponse.class);
        String adminToken = adminAuth.getAccessToken();
        
        // ===== 4단계: 관리자가 팝업스토어 생성 =====
        PopupStoreCreateRequest createRequest = PopupStoreCreateRequest.builder()
                .name("E2E 테스트 팝업스토어")
                .description("E2E 테스트를 위한 팝업스토어입니다")
                .imageUrl("https://example.com/e2e-test-image.jpg")
                .sourceUrl("https://example.com/e2e-test")
                .category("패션")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .location("서울시 강남구")
                .build();
        
        String createdStoreResponse = mockMvc.perform(post("/api/popupstores")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("E2E 테스트 팝업스토어"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        PopupStoreResponse createdStore = objectMapper.readValue(createdStoreResponse, PopupStoreResponse.class);
        Long popupStoreId = createdStore.getId();
        
        // ===== 5단계: 팝업스토어 상세 조회 =====
        String storeDetailResponse = mockMvc.perform(get("/api/popupstores/{id}", popupStoreId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(popupStoreId))
                .andExpect(jsonPath("$.name").value("E2E 테스트 팝업스토어"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // ===== 6단계: 조회수 증가 =====
        mockMvc.perform(post("/api/popupstores/{id}/view", popupStoreId))
                .andExpect(status().isOk());
        
        // ===== 7단계: 좋아요 증가 =====
        mockMvc.perform(post("/api/popupstores/{id}/like", popupStoreId))
                .andExpect(status().isOk());
        
        // ===== 8단계: 일반 사용자 로그인 =====
        LoginRequest userLoginRequest = new LoginRequest();
        userLoginRequest.setEmail("user@example.com");
        userLoginRequest.setPassword("user123");
        
        String userLoginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        LoginResponse userAuth = objectMapper.readValue(userLoginResponse, LoginResponse.class);
        String userToken = userAuth.getAccessToken();
        
        // ===== 9단계: 사용자가 예약 생성 =====
        ReservationCreateRequest reservationRequest = ReservationCreateRequest.builder()
                .memberId(1L)
                .popupStoreId(popupStoreId)
                .memberName("홍길동")
                .memberEmail("hong@example.com")
                .memberPhone("010-1234-5678")
                .reservationDateTime(LocalDateTime.now().plusDays(2))
                .numberOfPeople(2)
                .specialRequests("창가 자리 부탁드립니다")
                .notes("첫 방문입니다")
                .build();
        
        String reservationResponse = mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberName").value("홍길동"))
                .andExpect(jsonPath("$.popupStoreId").value(popupStoreId))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        ReservationResponse createdReservation = objectMapper.readValue(reservationResponse, ReservationResponse.class);
        Long reservationId = createdReservation.getId();
        
        // ===== 10단계: 사용자가 자신의 예약 목록 조회 =====
        mockMvc.perform(get("/api/reservations/member/{memberId}", 1L)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].memberId").value(1L));
        
        // ===== 11단계: 관리자가 예약 확인 =====
        mockMvc.perform(post("/api/reservations/{id}/confirm", reservationId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        
        // ===== 12단계: 확인된 예약 상태 확인 =====
        mockMvc.perform(get("/api/reservations/{id}", reservationId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
        
        // ===== 13단계: 관리자가 팝업스토어별 예약 목록 조회 =====
        mockMvc.perform(get("/api/reservations/popupstore/{popupStoreId}", popupStoreId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].popupStoreId").value(popupStoreId));
        
        // ===== 14단계: 사용자가 예약 취소 =====
        mockMvc.perform(post("/api/reservations/{id}/cancel", reservationId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\": \"개인 사정으로 취소\"}"))
                .andExpect(status().isOk());
        
        // ===== 15단계: 취소된 예약 상태 확인 =====
        mockMvc.perform(get("/api/reservations/{id}", reservationId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
        
        // ===== 16단계: 관리자가 팝업스토어 비활성화 =====
        mockMvc.perform(post("/api/admin/popupstores/{id}/deactivate", popupStoreId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        
        // ===== 17단계: 비활성화된 팝업스토어는 활성 목록에서 제외 확인 =====
        String updatedActiveStoresResponse = mockMvc.perform(get("/api/popupstores/active"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // 생성한 팝업스토어가 활성 목록에 없는지 확인
        assertThat(updatedActiveStoresResponse).doesNotContain("E2E 테스트 팝업스토어");
        
        // ===== 18단계: 로그아웃 =====
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
        
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("사용자 여행: 중복 예약 방지 시나리오")
    void testDuplicateReservationPreventionJourney() throws Exception {
        // ===== 1단계: 관리자 로그인 및 팝업스토어 생성 =====
        LoginRequest adminLoginRequest = new LoginRequest();
        adminLoginRequest.setEmail("admin@example.com");
        adminLoginRequest.setPassword("admin123");
        
        String adminLoginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLoginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        LoginResponse adminAuth = objectMapper.readValue(adminLoginResponse, LoginResponse.class);
        String adminToken = adminAuth.getAccessToken();
        
        PopupStoreCreateRequest createRequest = PopupStoreCreateRequest.builder()
                .name("중복 예약 테스트 팝업스토어")
                .description("중복 예약 방지 테스트")
                .imageUrl("https://example.com/duplicate-test.jpg")
                .sourceUrl("https://example.com/duplicate-test")
                .category("푸드")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .location("서울시 마포구")
                .build();
        
        String createdStoreResponse = mockMvc.perform(post("/api/popupstores")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        PopupStoreResponse createdStore = objectMapper.readValue(createdStoreResponse, PopupStoreResponse.class);
        Long popupStoreId = createdStore.getId();
        
        // ===== 2단계: 첫 번째 사용자 로그인 및 예약 =====
        LoginRequest user1LoginRequest = new LoginRequest();
        user1LoginRequest.setEmail("user1@example.com");
        user1LoginRequest.setPassword("user123");
        
        String user1LoginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1LoginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        LoginResponse user1Auth = objectMapper.readValue(user1LoginResponse, LoginResponse.class);
        String user1Token = user1Auth.getAccessToken();
        
        LocalDateTime reservationDateTime = LocalDateTime.now().plusDays(2);
        
        ReservationCreateRequest reservation1Request = ReservationCreateRequest.builder()
                .memberId(1L)
                .popupStoreId(popupStoreId)
                .memberName("김철수")
                .memberEmail("kim@example.com")
                .memberPhone("010-1111-2222")
                .reservationDateTime(reservationDateTime)
                .numberOfPeople(2)
                .build();
        
        mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation1Request)))
                .andExpect(status().isCreated());
        
        // ===== 3단계: 두 번째 사용자 로그인 및 같은 시간 예약 시도 =====
        LoginRequest user2LoginRequest = new LoginRequest();
        user2LoginRequest.setEmail("user2@example.com");
        user2LoginRequest.setPassword("user123");
        
        String user2LoginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2LoginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        LoginResponse user2Auth = objectMapper.readValue(user2LoginResponse, LoginResponse.class);
        String user2Token = user2Auth.getAccessToken();
        
        ReservationCreateRequest reservation2Request = ReservationCreateRequest.builder()
                .memberId(2L)
                .popupStoreId(popupStoreId)
                .memberName("이영희")
                .memberEmail("lee@example.com")
                .memberPhone("010-3333-4444")
                .reservationDateTime(reservationDateTime) // 같은 시간
                .numberOfPeople(1)
                .build();
        
        // ===== 4단계: 중복 예약 시도 시 에러 발생 확인 =====
        mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + user2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation2Request)))
                .andExpect(status().isBadRequest());
        
        // ===== 5단계: 다른 시간으로 예약 시도 =====
        ReservationCreateRequest reservation3Request = ReservationCreateRequest.builder()
                .memberId(2L)
                .popupStoreId(popupStoreId)
                .memberName("이영희")
                .memberEmail("lee@example.com")
                .memberPhone("010-3333-4444")
                .reservationDateTime(reservationDateTime.plusHours(1)) // 다른 시간
                .numberOfPeople(1)
                .build();
        
        mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + user2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation3Request)))
                .andExpect(status().isCreated());
    }
}


