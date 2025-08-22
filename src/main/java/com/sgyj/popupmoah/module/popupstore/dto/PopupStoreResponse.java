package com.sgyj.popupmoah.module.popupstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 팝업스토어 응답 DTO
 * 팝업스토어 조회 시 반환되는 데이터 전송 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupStoreResponse {

    private Long id;
    private String name;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private String category;
    private String storeNumber;
    private String website;
    private String email;
    private String sourceUrl;
    private String imageUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime reservationDate;
    private Boolean isActive;
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}

