package com.sgyj.popupmoah.reservation.application.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 예약 수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationUpdateRequest {

    /**
     * 회원 ID
     */
    @NotNull(message = "회원 ID는 필수입니다")
    private Long memberId;

    /**
     * 팝업스토어 ID
     */
    @NotNull(message = "팝업스토어 ID는 필수입니다")
    private Long popupStoreId;

    /**
     * 회원 이름
     */
    @NotBlank(message = "회원 이름은 필수입니다")
    @Size(max = 100, message = "회원 이름은 100자를 초과할 수 없습니다")
    private String memberName;

    /**
     * 회원 이메일
     */
    @NotBlank(message = "회원 이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 255, message = "이메일은 255자를 초과할 수 없습니다")
    private String memberEmail;

    /**
     * 회원 전화번호
     */
    @NotBlank(message = "회원 전화번호는 필수입니다")
    @Pattern(regexp = "^[0-9-+\\s()]+$", message = "올바른 전화번호 형식이 아닙니다")
    @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다")
    private String memberPhone;

    /**
     * 예약 날짜 및 시간
     */
    @NotNull(message = "예약 날짜 및 시간은 필수입니다")
    @Future(message = "예약 날짜 및 시간은 미래여야 합니다")
    private LocalDateTime reservationDateTime;

    /**
     * 예약 인원
     */
    @NotNull(message = "예약 인원은 필수입니다")
    @Min(value = 1, message = "예약 인원은 최소 1명 이상이어야 합니다")
    @Max(value = 20, message = "예약 인원은 최대 20명까지 가능합니다")
    private Integer numberOfPeople;

    /**
     * 특별 요청사항
     */
    @Size(max = 500, message = "특별 요청사항은 500자를 초과할 수 없습니다")
    private String specialRequests;

    /**
     * 메모
     */
    @Size(max = 1000, message = "메모는 1000자를 초과할 수 없습니다")
    private String notes;
}



