package com.sgyj.popupmoah.category.domain.entity;

import com.sgyj.popupmoah.core.entity.UpdatedEntity;
import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 카테고리 도메인 엔티티
 * 순수 Java로 구현된 도메인 객체
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Category extends UpdatedEntity {

    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private Double sortOrder;
    private Category parent;
    private List<Category> children;
    private List<PopupStore> popupStores;

    public void update(String name, String description, Double sortOrder, Boolean active) {
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
        this.active = active;
    }

    public void toggleActive() {
        this.active = !this.active;
    }

    public void changeSortOrder(Double newSortOrder) {
        this.sortOrder = newSortOrder;
    }

    // 계층형 구조 관련 메서드들
    public void setParent(Category parent) {
        this.parent = parent;
    }

    public void addChild(Category child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
        child.setParent(this);
    }

    public void removeChild(Category child) {
        if (this.children != null) {
            this.children.remove(child);
            child.setParent(null);
        }
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public boolean isLeaf() {
        return this.children == null || this.children.isEmpty();
    }

    public int getDepth() {
        if (isRoot()) {
            return 0;
        }
        return parent.getDepth() + 1;
    }

    public boolean isActive() {
        return active != null && active;
    }
} 