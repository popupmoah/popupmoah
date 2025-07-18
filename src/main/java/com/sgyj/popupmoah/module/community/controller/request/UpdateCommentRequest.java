package com.sgyj.popupmoah.module.community.controller.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class UpdateCommentRequest {
    @NotBlank
    private String content;
    private String currentUser;
} 