package com.sgyj.popupmoah.domain.category.entity;

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

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "sort_order", columnDefinition = "NUMERIC(10,2)")
    @Builder.Default
    private Double sortOrder = 0.0;

    // 계층형 구조: 부모 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // 계층형 구조: 자식 카테고리들
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PopupStore> popupStores = new ArrayList<>();

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
        this.children.add(child);
        child.setParent(this);
    }

    public void removeChild(Category child) {
        this.children.remove(child);
        child.setParent(null);
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    public int getDepth() {
        if (isRoot()) {
            return 0;
        }
        return parent.getDepth() + 1;
    }
} 