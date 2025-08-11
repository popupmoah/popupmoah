package com.sgyj.popupmoah.community.domain.aggregate;

import com.sgyj.popupmoah.community.domain.entity.Review;
import com.sgyj.popupmoah.community.domain.port.ReviewRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 어그리게이트 루트
 * 리뷰 도메인에 대한 접근을 제어하는 루트 엔티티
 */
public class ReviewAggregate {
    
    private final ReviewRepositoryPort repository;
    
    public ReviewAggregate(ReviewRepositoryPort repository) {
        this.repository = repository;
    }
    
    /**
     * 리뷰를 생성합니다.
     */
    public Review create(Review review) {
        // 도메인 규칙 검증
        validateReview(review);
        return repository.save(review);
    }
    
    /**
     * 리뷰를 조회합니다.
     */
    public Optional<Review> findById(Long id) {
        return repository.findById(id);
    }
    
    /**
     * 모든 리뷰를 조회합니다.
     */
    public List<Review> findAll() {
        return repository.findAll();
    }
    
    /**
     * 팝업스토어별 리뷰를 조회합니다.
     */
    public List<Review> findByPopupStoreId(Long popupStoreId) {
        return repository.findByPopupStoreId(popupStoreId);
    }
    
    /**
     * 멤버별 리뷰를 조회합니다.
     */
    public List<Review> findByMemberId(Long memberId) {
        return repository.findByMemberId(memberId);
    }
    
    /**
     * 리뷰를 업데이트합니다.
     */
    public Review update(Long id, Review review) {
        Optional<Review> existing = repository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("리뷰를 찾을 수 없습니다: " + id);
        }
        
        // 도메인 규칙 검증
        validateReview(review);
        
        Review updated = existing.get();
        // 불변성을 유지하면서 업데이트
        updated = Review.builder()
                .id(updated.getId())
                .popupStore(review.getPopupStore())
                .member(review.getMember())
                .content(review.getContent())
                .rating(review.getRating())
                .build();
        
        return repository.save(updated);
    }
    
    /**
     * 리뷰를 삭제합니다.
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("리뷰를 찾을 수 없습니다: " + id);
        }
        repository.deleteById(id);
    }
    
    /**
     * 리뷰 도메인 규칙을 검증합니다.
     */
    private void validateReview(Review review) {
        if (review.getContent() == null || review.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("리뷰 내용은 필수입니다.");
        }
        
        if (review.getRating() == null || !review.isValidRating()) {
            throw new IllegalArgumentException("평점은 1-5 사이의 값이어야 합니다.");
        }
        
        if (review.getPopupStore() == null) {
            throw new IllegalArgumentException("팝업스토어는 필수입니다.");
        }
        
        if (review.getMember() == null) {
            throw new IllegalArgumentException("멤버는 필수입니다.");
        }
    }
} 