package com.sgyj.popupmoah.module.community.controller.request;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

@Getter
@Setter
public class UpdateReviewRequest {
    @NotBlank
    private String content;

    @Min(1)
    @Max(5)
    private int rating;
}