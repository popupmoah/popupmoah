package com.sgyj.popupmoah.module.community.repository;

import com.sgyj.popupmoah.module.community.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPopupStoreId(Long popupStoreId);
} 