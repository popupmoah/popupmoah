package com.sgyj.popupmoah.domain.popupstore.entity;

import com.sgyj.popupmoah.domain.common.UpdatedEntity;
import com.sgyj.popupmoah.domain.category.entity.Category;
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

    @Column(columnDefinition = "NUMERIC(10,8)")
    private Double latitude;

    @Column(columnDefinition = "NUMERIC(11,8)")
    private Double longitude;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "reservation_date")
    private LocalDateTime reservationDate;

    @Column(name = "store_number", length = 20)
    private String storeNumber;

    @Column(length = 100)
    private String email;

    @Column(length = 500)
    private String website;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "popupStore", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PopupStoreImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "popupStore", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<com.sgyj.popupmoah.domain.community.entity.Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "popupStore", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<com.sgyj.popupmoah.domain.community.entity.Comment> comments = new ArrayList<>();

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

    public void updateLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void changeCategory(Category category) {
        this.category = category;
    }

    public void addImage(PopupStoreImage image) {
        this.images.add(image);
        image.setPopupStore(this);
    }

    public void removeImage(PopupStoreImage image) {
        this.images.remove(image);
        image.setPopupStore(null);
    }
} 