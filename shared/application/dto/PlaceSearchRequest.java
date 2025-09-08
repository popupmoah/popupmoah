package com.sgyj.popupmoah.shared.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 장소 검색 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceSearchRequest {

    private String query;                    // 검색 키워드
    private CoordinatesDto center;           // 검색 중심 좌표
    private Integer radius;                  // 검색 반경 (미터)
    private String category;                 // 카테고리 필터
    private Integer page;                    // 페이지 번호 (기본값: 1)
    private Integer size;                    // 페이지 크기 (기본값: 15)
    private String sort;                     // 정렬 방식 (distance, accuracy)

    /**
     * 기본값 설정
     */
    public PlaceSearchRequest withDefaults() {
        return PlaceSearchRequest.builder()
                .query(this.query)
                .center(this.center)
                .radius(this.radius != null ? this.radius : 1000) // 기본 1km
                .category(this.category)
                .page(this.page != null ? this.page : 1)
                .size(this.size != null ? this.size : 15)
                .sort(this.sort != null ? this.sort : "distance")
                .build();
    }
}

