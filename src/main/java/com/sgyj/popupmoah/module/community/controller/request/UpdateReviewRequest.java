package com.sgyj.popupmoah.module.community.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReviewRequest {
    private String content;
    private int rating;
} 