package com.sgyj.popupmoah.module.community.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewRequest {
    private Long popupStoreId;
    private String author;
    private String content;
    private int rating;
} 