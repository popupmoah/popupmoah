package com.sgyj.popupmoah.module.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 카테고리 요청 DTO
 * 카테고리 생성 및 수정 시 사용되는 데이터 전송 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    @Size(max = 100, message = "카테고리 이름은 100자를 초과할 수 없습니다.")
    private String name;

    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다.")
    private String description;

    private Long parentId;
    private Integer orderIndex;
    private Boolean isActive;
}

