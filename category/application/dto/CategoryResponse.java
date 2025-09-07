package com.sgyj.popupmoah.domain.category.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 카테고리 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String parentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CategoryResponse> children;
    private int childCount;
    private int level; // 계층 레벨 (0: 최상위, 1: 1단계 하위, ...)
}



