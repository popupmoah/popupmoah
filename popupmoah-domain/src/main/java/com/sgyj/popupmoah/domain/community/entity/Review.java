package com.sgyj.popupmoah.domain.community.entity;

import com.sgyj.popupmoah.domain.popupstore.entity.PopupStore;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review extends Object {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_store_id", nullable = false)
    private PopupStore popupStore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    public void updateReview(String newContent, Integer newRating) {
        this.content = newContent;
        this.rating = newRating;
    }

    public boolean isValidRating() {
        return rating != null && rating >= 1 && rating <= 5;
    }
} 