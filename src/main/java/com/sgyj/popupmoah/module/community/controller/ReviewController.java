package com.sgyj.popupmoah.module.community.controller;

import com.sgyj.popupmoah.module.community.entity.Review;
import com.sgyj.popupmoah.module.community.service.ReviewService;
import com.sgyj.popupmoah.module.community.controller.request.CreateReviewRequest;
import com.sgyj.popupmoah.module.community.controller.request.UpdateReviewRequest;
import com.sgyj.popupmoah.module.community.controller.response.ReviewResponse;
import com.sgyj.popupmoah.config.ApiResponse; // 표준 응답 DTO
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 등록 (표준 응답 포맷 적용)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(@RequestBody CreateReviewRequest request) {
        Review review = reviewService.createReview(request.getPopupStoreId(), request.getAuthor(), request.getContent(), request.getRating());
        return ResponseEntity.ok(ApiResponse.success(ReviewResponse.from(review)));
    }

    /**
     * 리뷰 수정 (표준 응답 포맷 적용)
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(@PathVariable Long reviewId, @RequestBody UpdateReviewRequest request) {
        Review review = reviewService.updateReview(reviewId, request.getContent(), request.getRating());
        return ResponseEntity.ok(ApiResponse.success(ReviewResponse.from(review)));
    }

    /**
     * 리뷰 삭제 (표준 응답 포맷 적용)
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success(null, "삭제 성공"));
    }

    @GetMapping("/popup/{popupStoreId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByPopupStore(@PathVariable Long popupStoreId) {
        List<ReviewResponse> responses = reviewService.getReviewsByPopupStore(popupStoreId)
                .stream().map(ReviewResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
} 