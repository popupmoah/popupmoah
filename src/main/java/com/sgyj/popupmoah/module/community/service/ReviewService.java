package com.sgyj.popupmoah.module.community.service;

import com.sgyj.popupmoah.module.community.entity.Review;
import com.sgyj.popupmoah.module.community.repository.ReviewRepository;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PopupStoreRepository popupStoreRepository;

    @Transactional
    public Review createReview(Long popupStoreId, String author, String content, Integer rating) {
        PopupStore popupStore = popupStoreRepository.findById(popupStoreId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팝업스토어입니다."));
        // TODO: 실제 Member 엔티티를 사용하도록 수정 필요
        Review review = Review.builder()
                .popupStore(popupStore)
                .member(null) // 임시로 null 설정
                .content(content)
                .rating(rating)
                .build();
        return reviewRepository.save(review);
    }

    @Transactional
    public Review updateReview(Long reviewId, String newContent, int newRating) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 후기입니다."));
        review.updateReview(newContent, newRating);
        return review;
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 후기입니다."));
        reviewRepository.deleteById(reviewId);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByPopupStore(Long popupStoreId) {
        return reviewRepository.findByPopupStoreId(popupStoreId);
    }
} 