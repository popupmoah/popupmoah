package com.sgyj.popupmoah.reservation.domain.entity;

import com.sgyj.popupmoah.core.entity.UpdatedEntity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 예약 도메인 엔티티
 * 팝업스토어 예약 정보를 관리하는 도메인 객체
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation extends UpdatedEntity {

    private Long id;
    private Long memberId;
    private Long popupStoreId;
    private String memberName;
    private String memberEmail;
    private String memberPhone;
    private LocalDateTime reservationDateTime;
    private Integer numberOfPeople;
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private String specialRequests;
    private String notes;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    /**
     * 예약 상태를 확인합니다.
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    /**
     * 예약이 확인되었는지 확인합니다.
     */
    public boolean isConfirmed() {
        return "CONFIRMED".equals(status);
    }

    /**
     * 예약이 취소되었는지 확인합니다.
     */
    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    /**
     * 예약이 완료되었는지 확인합니다.
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    /**
     * 예약을 확인 상태로 변경합니다.
     */
    public void confirm() {
        this.status = "CONFIRMED";
        this.confirmedAt = LocalDateTime.now();
    }

    /**
     * 예약을 취소합니다.
     */
    public void cancel(String reason) {
        this.status = "CANCELLED";
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    /**
     * 예약을 완료 상태로 변경합니다.
     */
    public void complete() {
        this.status = "COMPLETED";
    }

    /**
     * 예약이 유효한지 확인합니다.
     */
    public boolean isValid() {
        return reservationDateTime != null && 
               reservationDateTime.isAfter(LocalDateTime.now()) &&
               numberOfPeople != null && numberOfPeople > 0 &&
               memberId != null && popupStoreId != null;
    }

    /**
     * 예약 시간이 지났는지 확인합니다.
     */
    public boolean isExpired() {
        return reservationDateTime != null && 
               reservationDateTime.isBefore(LocalDateTime.now());
    }
}
