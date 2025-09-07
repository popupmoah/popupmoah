package com.sgyj.popupmoah.domain.community.infrastructure.web;

import com.sgyj.popupmoah.domain.community.application.dto.ReviewCreateRequest;
import com.sgyj.popupmoah.domain.community.application.dto.ReviewResponse;
import com.sgyj.popupmoah.domain.community.application.dto.ReviewUpdateRequest;
import com.sgyj.popupmoah.domain.community.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 리뷰 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 생성
     */
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewCreateRequest request) {
        log.info("리뷰 생성 API 호출: popupStoreId={}", request.getPopupStoreId());

        try {
            // 현재 인증된 사용자 ID 가져오기 (실제 구현에서는 JWT에서 추출)
            Long memberId = getCurrentMemberId();
            
            ReviewResponse response = reviewService.createReview(memberId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("리뷰 생성 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 리뷰 수정
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request) {
        log.info("리뷰 수정 API 호출: reviewId={}", reviewId);

        try {
            Long memberId = getCurrentMemberId();
            ReviewResponse response = reviewService.updateReview(reviewId, memberId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("리뷰 수정 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Object> deleteReview(@PathVariable Long reviewId) {
        log.info("리뷰 삭제 API 호출: reviewId={}", reviewId);

        try {
            Long memberId = getCurrentMemberId();
            reviewService.deleteReview(reviewId, memberId);
            return ResponseEntity.ok(Map.of(
                "message", "리뷰가 성공적으로 삭제되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.warn("리뷰 삭제 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 팝업스토어별 리뷰 목록 조회
     */
    @GetMapping("/popupstore/{popupStoreId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByPopupStore(@PathVariable Long popupStoreId) {
        log.info("팝업스토어 리뷰 목록 조회 API 호출: popupStoreId={}", popupStoreId);

        try {
            List<ReviewResponse> reviews = reviewService.getReviewsByPopupStore(popupStoreId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            log.error("팝업스토어 리뷰 목록 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 회원별 리뷰 목록 조회
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByMember(@PathVariable Long memberId) {
        log.info("회원 리뷰 목록 조회 API 호출: memberId={}", memberId);

        try {
            List<ReviewResponse> reviews = reviewService.getReviewsByMember(memberId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            log.error("회원 리뷰 목록 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 리뷰 상세 조회
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long reviewId) {
        log.info("리뷰 상세 조회 API 호출: reviewId={}", reviewId);

        try {
            ReviewResponse review = reviewService.getReview(reviewId);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            log.warn("리뷰 상세 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 팝업스토어 평점 정보 조회
     */
    @GetMapping("/popupstore/{popupStoreId}/rating")
    public ResponseEntity<Object> getRatingInfo(@PathVariable Long popupStoreId) {
        log.info("팝업스토어 평점 정보 조회 API 호출: popupStoreId={}", popupStoreId);

        try {
            Double averageRating = reviewService.getAverageRating(popupStoreId);
            Long reviewCount = reviewService.getReviewCount(popupStoreId);
            
            return ResponseEntity.ok(Map.of(
                "popupStoreId", popupStoreId,
                "averageRating", averageRating,
                "reviewCount", reviewCount,
                "hasRating", averageRating != null && averageRating > 0
            ));
        } catch (Exception e) {
            log.error("팝업스토어 평점 정보 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 현재 인증된 사용자 ID 가져오기 (임시 구현)
     */
    private Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        
        // 실제 구현에서는 JWT 토큰에서 memberId를 추출하거나
        // 사용자명으로 회원 정보를 조회하여 ID를 반환
        // 현재는 임시로 1을 반환
        return 1L; // TODO: 실제 memberId 추출 로직 구현
    }
}



