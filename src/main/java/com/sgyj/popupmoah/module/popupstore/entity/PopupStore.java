package com.sgyj.popupmoah.module.popupstore.entity;

import com.sgyj.popupmoah.infra.jpa.UpdatedEntity;
import com.sgyj.popupmoah.module.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "popup_store")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PopupStore extends UpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 8)
    private Double latitude;

    @Column(precision = 11, scale = 8)
    private Double longitude;

    /**
     * 팝업스토어 시작일시
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * 팝업스토어 종료일시
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * 팝업스토어 사전 예약 일시
     */
    @Column(name = "reservation_date")
    private LocalDateTime reservationDate;

    /**
     * 팝업스토어 전화번호
     */
    @Column(name = "store_number", length = 20)
    private String storeNumber;

    /**
     * 팝업스토어 대표 이메일
     */
    @Column(length = 100)
    private String email;

    /**
     * 팝업스토어 웹사이트
     */
    @Column(length = 500)
    private String website;

    /**
     * 카테고리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * 팝업스토어 이미지 목록
     */
    @OneToMany(mappedBy = "popupStore", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PopupStoreImage> images = new ArrayList<>();

    /**
     * 리뷰 목록
     */
    @OneToMany(mappedBy = "popupStore", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<com.sgyj.popupmoah.module.community.entity.Review> reviews = new ArrayList<>();

    /**
     * 댓글 목록
     */
    @OneToMany(mappedBy = "popupStore", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<com.sgyj.popupmoah.module.community.entity.Comment> comments = new ArrayList<>();

    /**
     * 팝업스토어 정보 업데이트
     */
    public void updateInfo(String name, String description, LocalDateTime startDate, 
                          LocalDateTime endDate, String storeNumber, String email, String website) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.storeNumber = storeNumber;
        this.email = email;
        this.website = website;
    }

    /**
     * 위치 정보 업데이트
     */
    public void updateLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * 카테고리 변경
     */
    public void changeCategory(Category category) {
        this.category = category;
    }

    /**
     * 이미지 추가
     */
    public void addImage(PopupStoreImage image) {
        this.images.add(image);
        image.setPopupStore(this);
    }

    /**
     * 이미지 제거
     */
    public void removeImage(PopupStoreImage image) {
        this.images.remove(image);
        image.setPopupStore(null);
    }
}
