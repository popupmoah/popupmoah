package com.sgyj.popupmoah.popupstore.application.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 팝업스토어 검색 요청 DTO
 * 검색 및 필터링 조건을 담는 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupStoreSearchRequest {

    /**
     * 검색 키워드 (이름, 설명, 위치 등)
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
     * 페이지 번호 (0부터 시작)
     */
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    private Integer page;

    /**
     * 페이지 크기
     */
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    private Integer size;

    /**
     * 정렬 기준 (name, startDate, endDate, viewCount, likeCount, createdAt)
     */
    private String sortBy;

    /**
     * 정렬 방향 (asc, desc)
     */
    private String sortDirection;
}
