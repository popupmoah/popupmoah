package com.sgyj.popupmoah.domain.community.service;

import com.sgyj.popupmoah.domain.community.repository.ReviewRepository;
import com.sgyj.popupmoah.domain.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.domain.popupstore.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 평점 계산 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingCalculationService {

    private final ReviewRepository reviewRepository;
    private final PopupStoreRepository popupStoreRepository;

    /**
     * 팝업스토어의 평점을 재계산하고 업데이트
     */
    @Transactional
    public void recalculateRating(Long popupStoreId) {
        log.info("평점 재계산 시작: popupStoreId={}", popupStoreId);

        // 팝업스토어 존재 확인
        PopupStore popupStore = popupStoreRepository.findById(popupStoreId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팝업스토어입니다."));

        // 평균 평점 계산
        Double averageRating = reviewRepository.getAverageRatingByPopupStoreId(popupStoreId);
        
        // 리뷰 개수 조회
        Long reviewCount = reviewRepository.getReviewCountByPopupStoreId(popupStoreId);

        // 평점 업데이트
        if (averageRating != null) {
            // 소수점 둘째 자리까지 반올림
            BigDecimal roundedRating = BigDecimal.valueOf(averageRating)
                    .setScale(2, RoundingMode.HALF_UP);
            
            popupStore.updateAverageRating(roundedRating.doubleValue());
            log.info("평균 평점 업데이트: popupStoreId={}, averageRating={}", popupStoreId, roundedRating);
        } else {
            popupStore.updateAverageRating(null);
            log.info("평점 없음으로 설정: popupStoreId={}", popupStoreId);
        }

        // 리뷰 개수 업데이트
        popupStore.updateReviewCount(reviewCount != null ? reviewCount : 0L);
        log.info("리뷰 개수 업데이트: popupStoreId={}, reviewCount={}", popupStoreId, reviewCount);

        log.info("평점 재계산 완료: popupStoreId={}", popupStoreId);
    }

    /**
     * 리뷰 생성 후 평점 업데이트
     */
    @Transactional
    public void updateRatingAfterReviewCreation(Long popupStoreId) {
        log.info("리뷰 생성 후 평점 업데이트: popupStoreId={}", popupStoreId);
        recalculateRating(popupStoreId);
    }

    /**
     * 리뷰 수정 후 평점 업데이트
     */
    @Transactional
    public void updateRatingAfterReviewUpdate(Long popupStoreId) {
        log.info("리뷰 수정 후 평점 업데이트: popupStoreId={}", popupStoreId);
        recalculateRating(popupStoreId);
    }

    /**
     * 리뷰 삭제 후 평점 업데이트
     */
    @Transactional
    public void updateRatingAfterReviewDeletion(Long popupStoreId) {
        log.info("리뷰 삭제 후 평점 업데이트: popupStoreId={}", popupStoreId);
        recalculateRating(popupStoreId);
    }

    /**
     * 팝업스토어의 현재 평점 정보 조회
     */
    public RatingInfo getRatingInfo(Long popupStoreId) {
        log.info("평점 정보 조회: popupStoreId={}", popupStoreId);

        PopupStore popupStore = popupStoreRepository.findById(popupStoreId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팝업스토어입니다."));

        return RatingInfo.builder()
                .popupStoreId(popupStoreId)
                .averageRating(popupStore.getAverageRating())
                .reviewCount(popupStore.getReviewCount())
                .hasRating(popupStore.hasRating())
                .build();
    }

    /**
     * 평점 정보 DTO
     */
    public static class RatingInfo {
        private Long popupStoreId;
        private Double averageRating;
        private Long reviewCount;
        private boolean hasRating;

        // Builder, Getter, Setter 생략 (Lombok 사용)
        public RatingInfo() {}

        public RatingInfo(Long popupStoreId, Double averageRating, Long reviewCount, boolean hasRating) {
            this.popupStoreId = popupStoreId;
            this.averageRating = averageRating;
            this.reviewCount = reviewCount;
            this.hasRating = hasRating;
        }

        public static RatingInfoBuilder builder() {
            return new RatingInfoBuilder();
        }

        public static class RatingInfoBuilder {
            private Long popupStoreId;
            private Double averageRating;
            private Long reviewCount;
            private boolean hasRating;

            RatingInfoBuilder() {}

            public RatingInfoBuilder popupStoreId(Long popupStoreId) {
                this.popupStoreId = popupStoreId;
                return this;
            }

            public RatingInfoBuilder averageRating(Double averageRating) {
                this.averageRating = averageRating;
                return this;
            }

            public RatingInfoBuilder reviewCount(Long reviewCount) {
                this.reviewCount = reviewCount;
                return this;
            }

            public RatingInfoBuilder hasRating(boolean hasRating) {
                this.hasRating = hasRating;
                return this;
            }

            public RatingInfo build() {
                return new RatingInfo(popupStoreId, averageRating, reviewCount, hasRating);
            }
        }

        // Getters and Setters
        public Long getPopupStoreId() { return popupStoreId; }
        public void setPopupStoreId(Long popupStoreId) { this.popupStoreId = popupStoreId; }
        
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
        
        public Long getReviewCount() { return reviewCount; }
        public void setReviewCount(Long reviewCount) { this.reviewCount = reviewCount; }
        
        public boolean isHasRating() { return hasRating; }
        public void setHasRating(boolean hasRating) { this.hasRating = hasRating; }
    }
}



