package com.sgyj.popupmoah.module.category.entity;

import com.sgyj.popupmoah.infra.generator.IdGenerator;
import com.sgyj.popupmoah.infra.jpa.UpdatedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends UpdatedEntity {

    @Id
    @IdGenerator
    private Long id;

    private String name;

    private String description;

    /**
     * 카테고리 활성화 상태
     */
    private boolean active;

    /**
     * <h2>카테고리 정렬
     * <p>정렬 순서는 낮은 숫자가 우선순위가 높음</p>
     * <p>이미 정렬된 카테고리 사이에 값이 껴야 될 경우 전체 변경이 되어야 하니 소수점으로 관리</p>
     * <p>ex) 1, 2, 3 -> 1, 1.1, 2, 3</p>
     */
    private float sortOrder;

    public static Category of(String name, String description, float sortOrder, boolean active) {
        Category category = new Category();
        category.name = name;
        category.description = description;
        category.sortOrder = sortOrder;
        category.active = active;
        return category;
    }
}
