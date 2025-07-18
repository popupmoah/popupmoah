package com.sgyj.popupmoah.module.community.controller.response;

import com.sgyj.popupmoah.module.community.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CommentResponse {
    private Long id;
    private Long popupStoreId;
    private String author;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> children;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .popupStoreId(comment.getPopupStore().getId())
                .author(comment.getAuthor())
                .content(comment.isDeleted() ? "삭제된 댓글입니다" : comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .children(null)
                .build();
    }

    public static CommentResponse from(Comment comment, List<CommentResponse> children) {
        return CommentResponse.builder()
                .id(comment.getId())
                .popupStoreId(comment.getPopupStore().getId())
                .author(comment.getAuthor())
                .content(comment.isDeleted() ? "삭제된 댓글입니다" : comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .children(children)
                .build();
    }
} 