package com.sgyj.popupmoah.module.category.entity;

import com.sgyj.popupmoah.infra.jpa.UpdatedEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Category extends UpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 카테고리 활성화 상태
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * 카테고리 정렬 순서
     * 정렬 순서는 낮은 숫자가 우선순위가 높음
     * 이미 정렬된 카테고리 사이에 값이 껴야 될 경우 전체 변경이 되어야 하니 소수점으로 관리
     * ex) 1, 2, 3 -> 1, 1.1, 2, 3
     */
    @Column(name = "sort_order", precision = 10, scale = 2)
    @Builder.Default
    private Double sortOrder = 0.0;

    /**
     * 이 카테고리에 속한 팝업스토어 목록
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @Builder.Default
    private List<com.sgyj.popupmoah.module.popupstore.entity.PopupStore> popupStores = new ArrayList<>();

    /**
     * 카테고리 정보를 수정하는 메서드
     */
    public void update(String name, String description, Double sortOrder, Boolean active) {
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
        this.active = active;
    }

    /**
     * 카테고리 활성화/비활성화
     */
    public void toggleActive() {
        this.active = !this.active;
    }

    /**
     * 정렬 순서 변경
     */
    public void changeSortOrder(Double newSortOrder) {
        this.sortOrder = newSortOrder;
    }
}
