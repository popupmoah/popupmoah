package com.sgyj.popupmoah.popupstore.domain.entity;

import com.sgyj.popupmoah.core.entity.UpdatedEntity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 팝업스토어 도메인 엔티티
 * 순수 Java로 구현된 도메인 객체
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopupStore extends UpdatedEntity {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String sourceUrl;
    private String category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String address;
    private Double latitude;
    private Double longitude;
    private Boolean active;
    private String status; // PENDING, APPROVED, REJECTED, ACTIVE, INACTIVE
    private String rejectionReason; // 거부 사유
    private Long viewCount;
    private Long likeCount;

    /**
     * 팝업스토어가 활성화되어 있는지 확인합니다.
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * 팝업스토어가 현재 진행 중인지 확인합니다.
     */
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return isActive() && 
               (startDate == null || now.isAfter(startDate)) && 
               (endDate == null || now.isBefore(endDate));
    }

    /**
     * 조회수를 증가시킵니다.
     */
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0L : this.viewCount) + 1L;
    }

    /**
     * 좋아요 수를 증가시킵니다.
     */
    public void incrementLikeCount() {
        this.likeCount = (this.likeCount == null ? 0L : this.likeCount) + 1L;
    }

    /**
     * 좋아요 수를 감소시킵니다.
     */
    public void decrementLikeCount() {
        if (this.likeCount != null && this.likeCount > 0) {
            this.likeCount = this.likeCount - 1L;
        }
    }


    /**
     * 팝업스토어가 좌표 정보를 가지고 있는지 확인합니다.
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    /**
     * 좌표 정보를 설정합니다.
     */
    public void setCoordinates(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * 주소 정보를 설정합니다.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    // ========== 관리자 기능 ==========

    /**
     * 팝업스토어를 승인합니다.
     */
    public void approve() {
        if (!"PENDING".equals(this.status)) {
            throw new IllegalStateException("승인 대기 상태가 아닌 팝업스토어입니다.");
        }
        this.status = "APPROVED";
        this.active = true;
    }

    /**
     * 팝업스토어를 거부합니다.
     */
    public void reject(String reason) {
        if (!"PENDING".equals(this.status)) {
            throw new IllegalStateException("승인 대기 상태가 아닌 팝업스토어입니다.");
        }
        this.status = "REJECTED";
        this.rejectionReason = reason;
        this.active = false;
    }

    /**
     * 팝업스토어를 비활성화합니다 (관리자용).
     */
    public void deactivate() {
        this.status = "INACTIVE";
        this.active = false;
    }

    /**
     * 팝업스토어를 재활성화합니다 (관리자용).
     */
    public void activate() {
        if ("APPROVED".equals(this.status) || "ACTIVE".equals(this.status)) {
            this.status = "ACTIVE";
            this.active = true;
        } else {
            throw new IllegalStateException("승인된 팝업스토어만 활성화할 수 있습니다.");
        }
    }

    /**
     * 팝업스토어 상태를 가져옵니다.
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * 거부 사유를 가져옵니다.
     */
    public String getRejectionReason() {
        return this.rejectionReason;
    }
} 