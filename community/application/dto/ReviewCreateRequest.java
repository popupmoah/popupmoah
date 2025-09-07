package com.sgyj.popupmoah.domain.community.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 리뷰 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {

    @NotNull(message = "팝업스토어 ID는 필수입니다.")
    private Long popupStoreId;

    @NotBlank(message = "리뷰 내용은 필수입니다.")
    private String content;

    @NotNull(message = "평점은 필수입니다.")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
    private Integer rating;
}



