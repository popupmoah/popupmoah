package com.sgyj.popupmoah.community.adapters.jpa;

import com.sgyj.popupmoah.core.entity.UpdatedEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 댓글 JPA 엔티티
 * 데이터베이스 매핑을 위한 인프라스트럭처 엔티티
 */
@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentJpaEntity extends UpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "popup_store_id", nullable = false)
    private Long popupStoreId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;
} 