package com.sgyj.popupmoah.category.adapters.jpa;

import com.sgyj.popupmoah.core.entity.UpdatedEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 카테고리 JPA 엔티티
 * 데이터베이스 매핑을 위한 인프라스트럭처 엔티티
 */
@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryJpaEntity extends UpdatedEntity {

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
    private CategoryJpaEntity parent;

    // 계층형 구조: 자식 카테고리들
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CategoryJpaEntity> children = new ArrayList<>();
} 