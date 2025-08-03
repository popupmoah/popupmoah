package com.sgyj.popupmoah.module.category.controller.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class RegisteredCategory {

    /**
     * 저장된 카테고리 식별자
     */
    private final Long id;
    /**
     * 카테고리 이름
     */
    private final String name;
    /**
     * 카테고리 설명
     */
    private final String description;

    /**
     * 카테고리 정렬 순서
     */
    private final Double sortOrder;

    /**
     * 카테고리 활성화 상태
     */
    private final Boolean active;

    public static RegisteredCategory of(Long id, String name, String description, Double sortOrder, Boolean active) {
        return new RegisteredCategory(id, name, description, sortOrder, active);
    }

    private RegisteredCategory(Long id, String name, String description, Double sortOrder, Boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
        this.active = active;
    }

}
