package com.sgyj.popupmoah.module.category.controller.request;

import com.sgyj.popupmoah.module.category.service.CategorySaveCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RegisterCategoryRequest {

    /**
     * 카테고리 이름
     */
    @NotNull(message = "카테고리 이름은 필수입니다.")
    private String name;
    /**
     * 카테고리 설명
     */
    private String description;

    /**
     * 카테고리 정렬 순서
     */
    private Float sortOrder;

    /**
     * 카테고리 활성화 상태
     */
    private Boolean active;

    public CategorySaveCommand mapToCommand() {
        return CategorySaveCommand.builder()
                .name(name)
                .description(description)
                .sortOrder(sortOrder != null ? sortOrder : 0.0) // 기본값 설정
                .active(active != null ? active : true) // 기본값 설정
                .build();
    }

}
