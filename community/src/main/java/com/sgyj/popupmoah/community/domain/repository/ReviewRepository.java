package com.sgyj.popupmoah.community.domain.repository;

import com.sgyj.popupmoah.community.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByPopupStoreId(Long popupStoreId);
    
    List<Review> findByMemberId(Long memberId);
} 