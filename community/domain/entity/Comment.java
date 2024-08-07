package com.sgyj.popupmoah.domain.community.entity;

import com.sgyj.popupmoah.domain.common.UpdatedEntity;
import com.sgyj.popupmoah.domain.popupstore.entity.PopupStore;
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