package com.sgyj.popupmoah.reservation.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 예약 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    /**
     * 예약 ID
     */
    private Long id;

    /**
     * 회원 ID
     */
    private Long memberId;

    /**
     * 팝업스토어 ID
     */
    private Long popupStoreId;

    /**
     * 회원 이름
     */
    private String memberName;

    /**
     * 회원 이메일
     */
    private String memberEmail;

    /**
     * 회원 전화번호
     */
    private String memberPhone;

    /**
     * 예약 날짜 및 시간
     */
    private LocalDateTime reservationDateTime;

    /**
     * 예약 인원
     */
    private Integer numberOfPeople;

    /**
     * 예약 상태 (PENDING, CONFIRMED, CANCELLED, COMPLETED)
     */
    private String status;

    /**
     * 특별 요청사항
     */
    private String specialRequests;

    /**
     * 메모
     */
    private String notes;

    /**
     * 확인 일시
     */
    private LocalDateTime confirmedAt;

    /**
     * 취소 일시
     */
    private LocalDateTime cancelledAt;

    /**
     * 취소 사유
     */
    private String cancellationReason;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;
}



