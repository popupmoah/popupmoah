package com.sgyj.popupmoah.module.community.service;

import com.sgyj.popupmoah.module.community.entity.Review;
import com.sgyj.popupmoah.module.community.repository.ReviewRepository;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private PopupStoreRepository popupStoreRepository;
    @InjectMocks
    private ReviewService reviewService;

    private PopupStore popupStore;
    private Review review;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        popupStore = PopupStore.builder().id(1L).build();
        review = Review.builder()
                .id(1L)
                .popupStore(popupStore)
                .author("작성자")
                .content("후기 내용")
                .rating(5)
                .build();
    }

    @Test
    void 후기_작성_성공() {
        when(popupStoreRepository.findById(1L)).thenReturn(Optional.of(popupStore));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        Review saved = reviewService.createReview(1L, "작성자", "후기 내용", 5);
        assertThat(saved.getAuthor()).isEqualTo("작성자");
        assertThat(saved.getContent()).isEqualTo("후기 내용");
        assertThat(saved.getRating()).isEqualTo(5);
    }

    @Test
    void 후기_작성_실패_존재하지_않는_팝업() {
        when(popupStoreRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(1L, "작성자", "후기 내용", 5));
    }

    @Test
    void 후기_수정_성공() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        Review updated = reviewService.updateReview(1L, "수정된 내용", 4);
        assertThat(updated.getContent()).isEqualTo("수정된 내용");
        assertThat(updated.getRating()).isEqualTo(4);
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    void 후기_수정_실패_존재하지_않는_후기() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> reviewService.updateReview(1L, "수정된 내용", 4));
    }

    @Test
    void 후기_삭제_성공() {
        doNothing().when(reviewRepository).deleteById(1L);
        reviewService.deleteReview(1L);
        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void 팝업별_후기_목록_조회() {
        when(reviewRepository.findByPopupStoreId(1L)).thenReturn(Arrays.asList(review));
        List<Review> reviews = reviewService.getReviewsByPopupStore(1L);
        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getAuthor()).isEqualTo("작성자");
    }
} 