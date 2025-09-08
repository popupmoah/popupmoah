package com.sgyj.popupmoah.popupstore.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 팝업스토어 검색 응답 DTO
 * 검색 결과와 페이징 정보를 담는 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupStoreSearchResponse {

    /**
     * 팝업스토어 목록
     */
    private List<PopupStoreResponse> popupStores;

    /**
     * 전체 개수
     */
    private Long totalElements;

    /**
     * 전체 페이지 수
     */
    private Integer totalPages;

    /**
     * 현재 페이지 번호
     */
    private Integer currentPage;

    /**
     * 페이지 크기
     */
    private Integer pageSize;

    /**
     * 첫 번째 페이지 여부
     */
    private Boolean first;

    /**
     * 마지막 페이지 여부
     */
    private Boolean last;

    /**
     * 검색 조건
     */
    private SearchCondition searchCondition;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCondition {
        /**
         * 검색 키워드
         */
        private String keyword;

        /**
         * 카테고리
         */
        private String category;

        /**
         * 위치
         */
        private String location;

        /**
         * 시작일 (이후)
         */
        private LocalDateTime startDateFrom;

        /**
         * 시작일 (이전)
         */
        private LocalDateTime startDateTo;

        /**
         * 종료일 (이후)
         */
        private LocalDateTime endDateFrom;

        /**
         * 종료일 (이전)
         */
        private LocalDateTime endDateTo;

        /**
         * 활성화 상태
         */
        private Boolean active;

        /**
         * 현재 진행 중인 팝업스토어만 조회
         */
        private Boolean currentlyActive;

        /**
         * 정렬 기준
         */
        private String sortBy;

        /**
         * 정렬 방향
         */
        private String sortDirection;
    }
}
