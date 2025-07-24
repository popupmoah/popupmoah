package com.sgyj.popupmoah.module.community.controller;

import com.sgyj.popupmoah.module.community.entity.Comment;
import com.sgyj.popupmoah.module.community.service.CommentService;
import com.sgyj.popupmoah.module.community.controller.request.CreateCommentRequest;
import com.sgyj.popupmoah.module.community.controller.request.UpdateCommentRequest;
import com.sgyj.popupmoah.module.community.controller.request.CreateReplyRequest;
import com.sgyj.popupmoah.module.community.controller.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.sgyj.popupmoah.module.community.security.JwtAuthentication;
import com.sgyj.popupmoah.config.ApiResponse; // 표준 응답 DTO

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 등록 (표준 응답 포맷 적용)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(@RequestBody CreateCommentRequest request) {
        Comment comment = commentService.createComment(request.getPopupStoreId(), request.getAuthor(), request.getContent());
        return ResponseEntity.ok(ApiResponse.success(CommentResponse.from(comment)));
    }

    /**
     * 대댓글 등록 (표준 응답 포맷 적용)
     */
    @PostMapping("/reply")
    public ResponseEntity<ApiResponse<CommentResponse>> createReply(@RequestBody CreateReplyRequest request) {
        Comment reply = commentService.createReply(request.getPopupStoreId(), request.getParentCommentId(), request.getAuthor(), request.getContent());
        return ResponseEntity.ok(ApiResponse.success(CommentResponse.from(reply)));
    }

    /**
     * 댓글 수정 (표준 응답 포맷 적용)
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(@PathVariable Long commentId, @RequestBody UpdateCommentRequest request, @AuthenticationPrincipal JwtAuthentication auth) {
        try {
            Comment comment = commentService.updateComment(commentId, request.getContent(), auth.getUsername());
            return ResponseEntity.ok(ApiResponse.success(CommentResponse.from(comment)));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.fail("권한이 없습니다.", "FORBIDDEN"));
        }
    }

    /**
     * 댓글 삭제 (표준 응답 포맷 적용)
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal JwtAuthentication auth) {
        try {
            commentService.deleteComment(commentId, auth.getUsername());
            return ResponseEntity.ok(ApiResponse.success(null, "삭제 성공"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.fail("권한이 없습니다.", "FORBIDDEN"));
        }
    }

    @GetMapping("/popup/{popupStoreId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPopupStore(@PathVariable Long popupStoreId) {
        List<CommentResponse> responses = commentService.getCommentsByPopupStore(popupStoreId)
                .stream().map(CommentResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/popup/{popupStoreId}/parents")
    public ResponseEntity<List<CommentResponse>> getParentCommentsByPopupStore(@PathVariable Long popupStoreId) {
        List<CommentResponse> responses = commentService.getParentCommentsByPopupStore(popupStoreId)
                .stream().map(CommentResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/replies/{parentCommentId}")
    public ResponseEntity<List<CommentResponse>> getRepliesByParent(@PathVariable Long parentCommentId) {
        List<CommentResponse> responses = commentService.getRepliesByParent(parentCommentId)
                .stream().map(CommentResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/popup/{popupStoreId}/tree")
    public ResponseEntity<List<CommentResponse>> getCommentTreeByPopupStore(@PathVariable Long popupStoreId) {
        List<Comment> parents = commentService.getCommentTreeByPopupStore(popupStoreId);
        List<CommentResponse> responses = parents.stream().map(parent -> toTreeResponse(parent)).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/popup/{popupStoreId}/tree/paged")
    public ResponseEntity<Page<CommentResponse>> getCommentTreeByPopupStoreWithPaging(
            @PathVariable Long popupStoreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> parentPage = commentService.getCommentTreeByPopupStoreWithPaging(popupStoreId, pageable);
        Page<CommentResponse> responsePage = parentPage.map(parent -> toTreeResponse(parent));
        return ResponseEntity.ok(responsePage);
    }

    private CommentResponse toTreeResponse(Comment comment) {
        List<Comment> children = commentService.getRepliesByParent(comment.getId());
        return CommentResponse.from(comment, children.isEmpty() ? null : children.stream().map(this::toTreeResponse).collect(Collectors.toList()));
    }
} 