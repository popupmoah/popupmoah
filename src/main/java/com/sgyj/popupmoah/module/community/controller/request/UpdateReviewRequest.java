package com.sgyj.popupmoah.module.community.controller.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Getter
@Setter
public class UpdateReviewRequest {
    @NotBlank
    private String content;

    @Min(1)
    @Max(5)
    private int rating;
}