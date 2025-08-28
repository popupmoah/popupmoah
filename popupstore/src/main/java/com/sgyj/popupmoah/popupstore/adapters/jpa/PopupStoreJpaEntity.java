package com.sgyj.popupmoah.popupstore.adapters.jpa;

import com.sgyj.popupmoah.core.entity.UpdatedEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 팝업스토어 JPA 엔티티
 * 데이터베이스 매핑을 위한 인프라스트럭처 엔티티
 */
@Entity
@Table(name = "popup_stores", indexes = {
    @Index(name = "idx_popup_stores_active", columnList = "active"),
    @Index(name = "idx_popup_stores_category", columnList = "category"),
    @Index(name = "idx_popup_stores_status", columnList = "status"),
    @Index(name = "idx_popup_stores_start_date", columnList = "start_date"),
    @Index(name = "idx_popup_stores_end_date", columnList = "end_date"),
    @Index(name = "idx_popup_stores_location", columnList = "location"),
    @Index(name = "idx_popup_stores_created_at", columnList = "created_at"),
    @Index(name = "idx_popup_stores_active_dates", columnList = "active, start_date, end_date"),
    @Index(name = "idx_popup_stores_category_active", columnList = "category, active"),
    @Index(name = "idx_popup_stores_status_created", columnList = "status, created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopupStoreJpaEntity extends UpdatedEntity {

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

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "like_count")
    @Builder.Default
    private Long likeCount = 0L;
} 