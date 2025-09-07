package com.sgyj.popupmoah.popupstore.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 팝업스토어 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupStoreResponse {

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 팝업스토어가 활성화되어 있는지 확인
     */
    public boolean isActive() {
        return active != null && active;
    }
    
    /**
     * 팝업스토어가 현재 진행 중인지 확인
     */
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return isActive() && 
               (startDate == null || now.isAfter(startDate)) && 
               (endDate == null || now.isBefore(endDate));
    }
}
