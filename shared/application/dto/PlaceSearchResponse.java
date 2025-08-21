package com.sgyj.popupmoah.shared.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 장소 검색 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceSearchResponse {

    private List<PlaceDto> places;           // 검색된 장소 목록
    private Integer totalCount;              // 전체 검색 결과 수
    private Integer page;                    // 현재 페이지
    private Integer size;                    // 페이지 크기
    private Boolean hasNext;                 // 다음 페이지 존재 여부
    private String searchProvider;           // 검색 제공자 (kakao, naver)

    /**
     * 검색된 장소 정보 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceDto {
        private String id;                   // 장소 ID
        private String name;                 // 장소명
        private String category;             // 카테고리
        private String phone;                // 전화번호
        private String address;              // 주소
        private String roadAddress;          // 도로명 주소
        private CoordinatesDto coordinates;  // 좌표
        private String url;                  // 장소 URL
        private String imageUrl;             // 이미지 URL
        private Double distance;             // 중심점으로부터의 거리 (미터)
        private String description;          // 설명
    }
}

