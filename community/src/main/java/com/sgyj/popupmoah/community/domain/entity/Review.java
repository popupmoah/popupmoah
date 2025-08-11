package com.sgyj.popupmoah.community.domain.entity;

import com.sgyj.popupmoah.core.entity.UpdatedEntity;
import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import lombok.*;

/**
 * 리뷰 도메인 엔티티
 * 순수 Java로 구현된 도메인 객체
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review extends UpdatedEntity {

    private Long id;
    private PopupStore popupStore;
    private Member member;
    private String content;
    private Integer rating;

    public void updateReview(String newContent, Integer newRating) {
        this.content = newContent;
        this.rating = newRating;
    }

    public boolean isValidRating() {
        return rating != null && rating >= 1 && rating <= 5;
    }
} 