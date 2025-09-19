package com.sgyj.popupmoah.popupstore.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 팝업스토어 엔티티
 * 팝업스토어의 기본 정보를 관리합니다.
 */
@Entity
@Table(name = "popup_stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopupStore extends Object {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "like_count")
    @Builder.Default
    private Long likeCount = 0L;

    /**
     * 팝업스토어가 활성화되어 있는지 확인합니다.
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * 팝업스토어가 현재 진행 중인지 확인합니다.
     */
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return isActive() && 
               (startDate == null || now.isAfter(startDate)) && 
               (endDate == null || now.isBefore(endDate));
    }

    /**
     * 조회수를 증가시킵니다.
     */
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0L : this.viewCount) + 1L;
    }

    /**
     * 좋아요 수를 증가시킵니다.
     */
    public void incrementLikeCount() {
        this.likeCount = (this.likeCount == null ? 0L : this.likeCount) + 1L;
    }

    /**
     * 좋아요 수를 감소시킵니다.
     */
    public void decrementLikeCount() {
        if (this.likeCount != null && this.likeCount > 0) {
            this.likeCount = this.likeCount - 1L;
        }
    }
} 