package com.sgyj.popupmoah.domain.popupstore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "popup_store_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PopupStoreImage extends Object {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_store_id", nullable = false)
    private PopupStore popupStore;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "image_order")
    private Integer imageOrder;

    @Column(name = "is_main")
    private Boolean isMain;

    public void setPopupStore(PopupStore popupStore) {
        this.popupStore = popupStore;
    }

    public void changeOrder(Integer order) {
        this.imageOrder = order;
    }

    public void setAsMain(Boolean isMain) {
        this.isMain = isMain;
    }
} 