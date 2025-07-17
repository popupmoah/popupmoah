package com.sgyj.popupmoah.module.community.controller;

import com.sgyj.popupmoah.module.community.entity.Review;
import com.sgyj.popupmoah.module.community.service.ReviewService;
import com.sgyj.popupmoah.module.community.controller.request.CreateReviewRequest;
import com.sgyj.popupmoah.module.community.controller.request.UpdateReviewRequest;
import com.sgyj.popupmoah.module.community.controller.response.ReviewResponse;
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

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody CreateReviewRequest request) {
        Review review = reviewService.createReview(request.getPopupStoreId(), request.getAuthor(), request.getContent(), request.getRating());
        return ResponseEntity.ok(ReviewResponse.from(review));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long reviewId, @RequestBody UpdateReviewRequest request) {
        Review review = reviewService.updateReview(reviewId, request.getContent(), request.getRating());
        return ResponseEntity.ok(ReviewResponse.from(review));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/popup/{popupStoreId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByPopupStore(@PathVariable Long popupStoreId) {
        List<ReviewResponse> responses = reviewService.getReviewsByPopupStore(popupStoreId)
                .stream().map(ReviewResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
} 