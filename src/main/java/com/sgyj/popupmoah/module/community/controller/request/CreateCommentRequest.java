package com.sgyj.popupmoah.module.community.controller.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CreateCommentRequest {
    @NotNull
    private Long popupStoreId;

    @NotBlank
    private String author;

    @NotBlank
    private String content;
} 