package com.sgyj.popupmoah.module.community.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReplyRequest {
    private Long popupStoreId;
    private Long parentCommentId;
    private String author;
    private String content;
} 