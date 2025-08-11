package com.sgyj.popupmoah.community.domain.entity;

import com.sgyj.popupmoah.core.entity.UpdatedEntity;
import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import lombok.*;

/**
 * 댓글 도메인 엔티티
 * 순수 Java로 구현된 도메인 객체
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment extends UpdatedEntity {

    private Long id;
    private PopupStore popupStore;
    private Member member;
    private Comment parent;
    private String content;
    private Boolean deleted;

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void softDelete() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public boolean hasParent() {
        return this.parent != null;
    }
} 