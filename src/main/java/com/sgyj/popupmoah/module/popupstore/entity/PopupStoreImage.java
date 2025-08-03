package com.sgyj.popupmoah.module.popupstore.entity;

import com.sgyj.popupmoah.infra.jpa.UpdatedEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "popup_store_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PopupStoreImage extends UpdatedEntity {
    
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

    /**
     * 팝업스토어 설정
     */
    public void setPopupStore(PopupStore popupStore) {
        this.popupStore = popupStore;
    }

    /**
     * 이미지 순서 변경
     */
    public void changeOrder(Integer order) {
        this.imageOrder = order;
    }

    /**
     * 메인 이미지 설정
     */
    public void setAsMain(Boolean isMain) {
        this.isMain = isMain;
    }
} 