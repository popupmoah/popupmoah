package com.sgyj.popupmoah.shared.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 장소 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceInfo {
    
    /**
     * 장소 ID
     */
    private String id;
    
    /**
     * 장소명
     */
    private String name;
    
    /**
     * 카테고리
     */
    private String category;
    
    /**
     * 전화번호
     */
    private String phone;
    
    /**
     * 주소
     */
    private String address;
    
    /**
     * 도로명 주소
     */
    private String roadAddress;
    
    /**
     * 좌표
     */
    private CoordinatesDto coordinates;
    
    /**
     * 장소 URL
     */
    private String url;
    
    /**
     * 이미지 URL
     */
    private String imageUrl;
    
    /**
     * 거리 (미터)
     */
    private Double distance;
    
    /**
     * 설명
     */
    private String description;
}

