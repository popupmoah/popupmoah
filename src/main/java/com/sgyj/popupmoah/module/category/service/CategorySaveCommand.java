package com.sgyj.popupmoah.module.category.service;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategorySaveCommand {

    /**
     * 카테고리 이름
     */
    @NotNull(message = "카테고리 이름은 필수입니다.")
    private final String name;
    /**
     * 카테고리 설명
     */
    private final String description;

    /**
     * 카테고리 정렬 순서
     */
    private final float sortOrder;

    /**
     * 카테고리 활성화 상태
     */
    private final boolean active;

    /**
     * 카테고리 저장 명령 객체 생성 팩토리 메서드
     *
     * @param name        카테고리 이름
     * @param description 카테고리 설명
     * @param sortOrder   카테고리 정렬 순서
     * @param active      카테고리 활성화 상태
     * @return CategorySaveCommand 객체
     */
    public static CategorySaveCommand of(String name, String description, Float sortOrder, Boolean active) {
        return CategorySaveCommand.builder()
                .name(name)
                .description(description)
                .sortOrder(sortOrder != null ? sortOrder : 1.0f) // 기본 정렬 순서
                .active(active != null ? active : true) // 기본 활성화 상태
                .build();
    }

}
