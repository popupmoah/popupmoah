package com.sgyj.popupmoah.module.community.controller.request;

import lombok.Getter;
import lombok.Setter;
// javax.validation → jakarta.validation 으로 변경 (Java 21/Spring Boot 3.x)
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