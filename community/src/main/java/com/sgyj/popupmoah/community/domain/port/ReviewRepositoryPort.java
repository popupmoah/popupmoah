package com.sgyj.popupmoah.community.domain.port;

import com.sgyj.popupmoah.community.domain.entity.Review;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 리포지토리 포트
 * 도메인이 정의하는 인터페이스로, 인프라스트럭처가 구현
 */
public interface ReviewRepositoryPort {
    
    /**
     * 리뷰를 저장합니다.
     */
    Review save(Review review);
    
    /**
     * ID로 리뷰를 조회합니다.
     */
    Optional<Review> findById(Long id);
    
    /**
     * 모든 리뷰를 조회합니다.
     */
    List<Review> findAll();
    
    /**
     * 팝업스토어별 리뷰를 조회합니다.
     */
    List<Review> findByPopupStoreId(Long popupStoreId);
    
    /**
     * 멤버별 리뷰를 조회합니다.
     */
    List<Review> findByMemberId(Long memberId);
    
    /**
     * 리뷰를 삭제합니다.
     */
    void deleteById(Long id);
    
    /**
     * 리뷰가 존재하는지 확인합니다.
     */
    boolean existsById(Long id);
} 