package com.sgyj.popupmoah.community.adapters.jpa;

import com.sgyj.popupmoah.core.entity.UpdatedEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 리뷰 JPA 엔티티
 * 데이터베이스 매핑을 위한 인프라스트럭처 엔티티
 */
@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewJpaEntity extends UpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "popup_store_id", nullable = false)
    private Long popupStoreId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rating;
} 