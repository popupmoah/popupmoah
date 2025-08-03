package com.sgyj.popupmoah.module.popupstore.repository;

import com.sgyj.popupmoah.module.popupstore.entity.PopupStoreImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PopupStoreImageRepository extends JpaRepository<PopupStoreImage, Long> {
    List<PopupStoreImage> findByPopupStoreId(Long popupStoreId);
} 