package com.sgyj.popupmoah.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgyj.popupmoah.popupstore.application.dto.PopupStoreCreateRequest;
import com.sgyj.popupmoah.popupstore.application.dto.PopupStoreResponse;
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
class PopupStoreReservationIntegrationTest {
    
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
    @DisplayName("팝업스토어 생성 후 예약 생성 통합 테스트")
    void testCreatePopupStoreAndReservation() throws Exception {
        // Given - 팝업스토어 생성
        PopupStoreCreateRequest popupStoreRequest = PopupStoreCreateRequest.builder()
                .name("테스트 팝업스토어")
                .description("통합 테스트용 팝업스토어")
                .imageUrl("https://example.com/image.jpg")
                .sourceUrl("https://example.com")
                .category("패션")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .location("서울시 강남구")
                .build();
        
        // When - 팝업스토어 생성
        String popupStoreResponse = mockMvc.perform(post("/api/popupstores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(popupStoreRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("테스트 팝업스토어"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        PopupStoreResponse createdPopupStore = objectMapper.readValue(popupStoreResponse, PopupStoreResponse.class);
        Long popupStoreId = createdPopupStore.getId();
        
        // Then - 팝업스토어가 생성되었는지 확인
        assertThat(popupStoreId).isNotNull();
        
        // Given - 예약 생성
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
        
        // When - 예약 생성
        String reservationResponse = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberName").value("홍길동"))
                .andExpect(jsonPath("$.popupStoreId").value(popupStoreId))
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        ReservationResponse createdReservation = objectMapper.readValue(reservationResponse, ReservationResponse.class);
        Long reservationId = createdReservation.getId();
        
        // Then - 예약이 생성되었는지 확인
        assertThat(reservationId).isNotNull();
        
        // When - 생성된 예약 조회
        mockMvc.perform(get("/api/reservations/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.memberName").value("홍길동"))
                .andExpect(jsonPath("$.popupStoreId").value(popupStoreId));
        
        // When - 팝업스토어별 예약 목록 조회
        mockMvc.perform(get("/api/reservations/popupstore/{popupStoreId}", popupStoreId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].popupStoreId").value(popupStoreId));
    }
    
    @Test
    @DisplayName("팝업스토어 조회수 증가 후 예약 생성 통합 테스트")
    void testIncrementViewCountAndCreateReservation() throws Exception {
        // Given - 팝업스토어 생성
        PopupStoreCreateRequest popupStoreRequest = PopupStoreCreateRequest.builder()
                .name("조회수 테스트 팝업스토어")
                .description("조회수 증가 테스트용")
                .imageUrl("https://example.com/image.jpg")
                .sourceUrl("https://example.com")
                .category("뷰티")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .location("서울시 서초구")
                .build();
        
        String popupStoreResponse = mockMvc.perform(post("/api/popupstores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(popupStoreRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        PopupStoreResponse createdPopupStore = objectMapper.readValue(popupStoreResponse, PopupStoreResponse.class);
        Long popupStoreId = createdPopupStore.getId();
        
        // When - 조회수 증가
        mockMvc.perform(post("/api/popupstores/{id}/view", popupStoreId))
                .andExpect(status().isOk());
        
        // When - 팝업스토어 조회하여 조회수 확인
        String updatedPopupStoreResponse = mockMvc.perform(get("/api/popupstores/{id}", popupStoreId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        PopupStoreResponse updatedPopupStore = objectMapper.readValue(updatedPopupStoreResponse, PopupStoreResponse.class);
        
        // Then - 조회수가 증가했는지 확인
        assertThat(updatedPopupStore.getViewCount()).isGreaterThan(0);
        
        // Given - 예약 생성
        ReservationCreateRequest reservationRequest = ReservationCreateRequest.builder()
                .memberId(2L)
                .popupStoreId(popupStoreId)
                .memberName("김철수")
                .memberEmail("kim@example.com")
                .memberPhone("010-9876-5432")
                .reservationDateTime(LocalDateTime.now().plusDays(3))
                .numberOfPeople(1)
                .build();
        
        // When - 예약 생성
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequest)))
                .andExpect(status().isCreated());
    }
    
    @Test
    @DisplayName("예약 생성 후 상태 변경 통합 테스트")
    void testCreateReservationAndChangeStatus() throws Exception {
        // Given - 팝업스토어 생성
        PopupStoreCreateRequest popupStoreRequest = PopupStoreCreateRequest.builder()
                .name("상태 변경 테스트 팝업스토어")
                .description("예약 상태 변경 테스트용")
                .imageUrl("https://example.com/image.jpg")
                .sourceUrl("https://example.com")
                .category("푸드")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .location("서울시 마포구")
                .build();
        
        String popupStoreResponse = mockMvc.perform(post("/api/popupstores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(popupStoreRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        PopupStoreResponse createdPopupStore = objectMapper.readValue(popupStoreResponse, PopupStoreResponse.class);
        Long popupStoreId = createdPopupStore.getId();
        
        // Given - 예약 생성
        ReservationCreateRequest reservationRequest = ReservationCreateRequest.builder()
                .memberId(3L)
                .popupStoreId(popupStoreId)
                .memberName("이영희")
                .memberEmail("lee@example.com")
                .memberPhone("010-1111-2222")
                .reservationDateTime(LocalDateTime.now().plusDays(2))
                .numberOfPeople(3)
                .build();
        
        String reservationResponse = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        ReservationResponse createdReservation = objectMapper.readValue(reservationResponse, ReservationResponse.class);
        Long reservationId = createdReservation.getId();
        
        // When - 예약 확인
        mockMvc.perform(post("/api/reservations/{id}/confirm", reservationId))
                .andExpect(status().isOk());
        
        // When - 확인된 예약 조회
        mockMvc.perform(get("/api/reservations/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
        
        // When - 예약 취소
        mockMvc.perform(post("/api/reservations/{id}/cancel", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\": \"개인 사정으로 취소\"}"))
                .andExpect(status().isOk());
        
        // When - 취소된 예약 조회
        mockMvc.perform(get("/api/reservations/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
    
    @Test
    @DisplayName("중복 예약 방지 통합 테스트")
    void testDuplicateReservationPrevention() throws Exception {
        // Given - 팝업스토어 생성
        PopupStoreCreateRequest popupStoreRequest = PopupStoreCreateRequest.builder()
                .name("중복 예약 테스트 팝업스토어")
                .description("중복 예약 방지 테스트용")
                .imageUrl("https://example.com/image.jpg")
                .sourceUrl("https://example.com")
                .category("라이프스타일")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .location("서울시 송파구")
                .build();
        
        String popupStoreResponse = mockMvc.perform(post("/api/popupstores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(popupStoreRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        PopupStoreResponse createdPopupStore = objectMapper.readValue(popupStoreResponse, PopupStoreResponse.class);
        Long popupStoreId = createdPopupStore.getId();
        
        LocalDateTime reservationDateTime = LocalDateTime.now().plusDays(2);
        
        // Given - 첫 번째 예약 생성
        ReservationCreateRequest firstReservationRequest = ReservationCreateRequest.builder()
                .memberId(4L)
                .popupStoreId(popupStoreId)
                .memberName("박민수")
                .memberEmail("park@example.com")
                .memberPhone("010-3333-4444")
                .reservationDateTime(reservationDateTime)
                .numberOfPeople(2)
                .build();
        
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstReservationRequest)))
                .andExpect(status().isCreated());
        
        // Given - 같은 시간에 두 번째 예약 시도
        ReservationCreateRequest secondReservationRequest = ReservationCreateRequest.builder()
                .memberId(5L)
                .popupStoreId(popupStoreId)
                .memberName("최지영")
                .memberEmail("choi@example.com")
                .memberPhone("010-5555-6666")
                .reservationDateTime(reservationDateTime)
                .numberOfPeople(1)
                .build();
        
        // When & Then - 중복 예약 시도 시 에러 발생
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondReservationRequest)))
                .andExpect(status().isBadRequest());
    }
}


