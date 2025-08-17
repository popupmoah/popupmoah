package com.sgyj.popupmoah.domain.community.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    @NotNull(message = "팝업스토어 ID는 필수입니다.")
    private Long popupStoreId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;

    private Long parentId; // 부모 댓글 ID (대댓글인 경우)
}
