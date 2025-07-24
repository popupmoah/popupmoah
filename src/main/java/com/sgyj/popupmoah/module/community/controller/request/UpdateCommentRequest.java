package com.sgyj.popupmoah.module.community.controller.request;

import lombok.Getter;
import lombok.Setter;
// javax.validation → jakarta.validation 으로 변경 (Java 21/Spring Boot 3.x)
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class UpdateCommentRequest {
    @NotBlank
    private String content;
} 