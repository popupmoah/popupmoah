package com.sgyj.popupmoah.module.community.controller.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Getter
@Setter
public class CreateReviewRequest {
    @NotNull
    private Long popupStoreId;

    @NotBlank
    private String author;

    @NotBlank
    private String content;

    @Min(1)
    @Max(5)
    private int rating;
} 