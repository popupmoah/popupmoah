package com.sgyj.popupmoah.category.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Category {

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

    // 감사 필드들
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String name, String description, Double sortOrder, Boolean active) {
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    public void toggleActive() {
        this.active = !this.active;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeSortOrder(Double newSortOrder) {
        this.sortOrder = newSortOrder;
        this.updatedAt = LocalDateTime.now();
    }

    // 계층형 구조 관련 메서드들
    public void setParent(Category parent) {
        this.parent = parent;
        this.updatedAt = LocalDateTime.now();
    }

    public void addChild(Category child) {
        this.children.add(child);
        child.setParent(this);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeChild(Category child) {
        this.children.remove(child);
        child.setParent(null);
        this.updatedAt = LocalDateTime.now();
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
