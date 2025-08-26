package com.sgyj.popupmoah.domain.community.service;

import com.sgyj.popupmoah.domain.community.application.dto.ReviewCreateRequest;
import com.sgyj.popupmoah.domain.community.application.dto.ReviewResponse;
import com.sgyj.popupmoah.domain.community.application.dto.ReviewUpdateRequest;
import com.sgyj.popupmoah.domain.community.entity.Review;
import com.sgyj.popupmoah.domain.community.repository.ReviewRepository;
import com.sgyj.popupmoah.domain.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.domain.popupstore.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 리뷰 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PopupStoreRepository popupStoreRepository;
    private final MemberService memberService;
    private final RatingCalculationService ratingCalculationService;

    /**
     * 리뷰 생성
     */
    @Transactional
    public ReviewResponse createReview(Long memberId, ReviewCreateRequest request) {
        log.info("리뷰 생성 요청: memberId={}, popupStoreId={}", memberId, request.getPopupStoreId());

        // 팝업스토어 존재 확인
        PopupStore popupStore = popupStoreRepository.findById(request.getPopupStoreId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팝업스토어입니다."));

        // 회원 존재 확인
        var member = memberService.findById(memberId);

        // 이미 리뷰를 작성했는지 확인
        if (reviewRepository.existsByPopupStoreIdAndMemberId(request.getPopupStoreId(), memberId)) {
            throw new IllegalArgumentException("이미 리뷰를 작성했습니다.");
        }

        // 리뷰 생성
        Review review = Review.builder()
                .popupStore(popupStore)
                .member(member)
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        Review savedReview = reviewRepository.save(review);

        // 평점 재계산
        ratingCalculationService.updateRatingAfterReviewCreation(request.getPopupStoreId());

        log.info("리뷰 생성 완료: reviewId={}, memberId={}, popupStoreId={}", 
                savedReview.getId(), memberId, request.getPopupStoreId());

        return convertToResponse(savedReview);
    }

    /**
     * 리뷰 수정
     */
    @Transactional
    public ReviewResponse updateReview(Long reviewId, Long memberId, ReviewUpdateRequest request) {
        log.info("리뷰 수정 요청: reviewId={}, memberId={}", reviewId, memberId);

        Review review = findById(reviewId);

        // 권한 확인 (본인이 작성한 리뷰만 수정 가능)
        if (!review.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("리뷰를 수정할 권한이 없습니다.");
        }

        // 리뷰 수정
        review.updateReview(request.getContent(), request.getRating());

        // 평점 재계산
        ratingCalculationService.updateRatingAfterReviewUpdate(review.getPopupStore().getId());

        log.info("리뷰 수정 완료: reviewId={}, memberId={}", reviewId, memberId);

        return convertToResponse(review);
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public void deleteReview(Long reviewId, Long memberId) {
        log.info("리뷰 삭제 요청: reviewId={}, memberId={}", reviewId, memberId);

        Review review = findById(reviewId);

        // 권한 확인 (본인이 작성한 리뷰만 삭제 가능)
        if (!review.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("리뷰를 삭제할 권한이 없습니다.");
        }

        Long popupStoreId = review.getPopupStore().getId();
        reviewRepository.delete(review);

        // 평점 재계산
        ratingCalculationService.updateRatingAfterReviewDeletion(popupStoreId);

        log.info("리뷰 삭제 완료: reviewId={}, memberId={}", reviewId, memberId);
    }

    /**
     * 팝업스토어별 리뷰 목록 조회
     */
    public List<ReviewResponse> getReviewsByPopupStore(Long popupStoreId) {
        log.info("팝업스토어 리뷰 목록 조회: popupStoreId={}", popupStoreId);

        List<Review> reviews = reviewRepository.findByPopupStoreId(popupStoreId);
        
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 회원별 리뷰 목록 조회
     */
    public List<ReviewResponse> getReviewsByMember(Long memberId) {
        log.info("회원 리뷰 목록 조회: memberId={}", memberId);

        List<Review> reviews = reviewRepository.findByMemberId(memberId);
        
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 상세 조회
     */
    public ReviewResponse getReview(Long reviewId) {
        log.info("리뷰 상세 조회: reviewId={}", reviewId);

        Review review = findById(reviewId);
        return convertToResponse(review);
    }

    /**
     * 팝업스토어 평균 평점 조회
     */
    public Double getAverageRating(Long popupStoreId) {
        return reviewRepository.getAverageRatingByPopupStoreId(popupStoreId);
    }

    /**
     * 팝업스토어 리뷰 개수 조회
     */
    public Long getReviewCount(Long popupStoreId) {
        return reviewRepository.getReviewCountByPopupStoreId(popupStoreId);
    }

    /**
     * 리뷰 ID로 조회
     */
    private Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));
    }

    /**
     * Review 엔티티를 ReviewResponse로 변환
     */
    private ReviewResponse convertToResponse(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .popupStoreId(review.getPopupStore().getId())
                .memberId(review.getMember().getId())
                .memberUsername(review.getMember().getUsername())
                .memberNickname(review.getMember().getNickname())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .comments(List.of()) // 댓글은 별도로 조회
                .commentCount(0) // 댓글 수는 별도로 계산
                .build();
    }
}
