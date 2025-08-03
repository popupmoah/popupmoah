package com.sgyj.popupmoah.module.community.entity;

import com.sgyj.popupmoah.infra.jpa.UpdatedEntity;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment extends UpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_store_id", nullable = false)
    private PopupStore popupStore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    /**
     * 댓글 내용 업데이트
     */
    public void updateContent(String newContent) {
        this.content = newContent;
    }

    /**
     * 댓글 삭제 (소프트 삭제)
     */
    public void softDelete() {
        this.deleted = true;
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return deleted != null && deleted;
    }

    /**
     * 부모 댓글 확인
     */
    public boolean hasParent() {
        return parent != null;
    }
} 