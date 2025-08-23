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
     * 팝업스토어를 비활성화합니다.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * 팝업스토어를 활성화합니다.
     */
    public void activate() {
        this.active = true;
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
} 