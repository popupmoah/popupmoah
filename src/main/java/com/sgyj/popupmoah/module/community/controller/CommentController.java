package com.sgyj.popupmoah.module.community.controller;

import com.sgyj.popupmoah.module.community.entity.Comment;
import com.sgyj.popupmoah.module.community.service.CommentService;
import com.sgyj.popupmoah.module.community.controller.request.CreateCommentRequest;
import com.sgyj.popupmoah.module.community.controller.request.UpdateCommentRequest;
import com.sgyj.popupmoah.module.community.controller.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CreateCommentRequest request) {
        Comment comment = commentService.createComment(request.getPopupStoreId(), request.getAuthor(), request.getContent());
        return ResponseEntity.ok(CommentResponse.from(comment));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId, @RequestBody UpdateCommentRequest request) {
        Comment comment = commentService.updateComment(commentId, request.getContent());
        return ResponseEntity.ok(CommentResponse.from(comment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/popup/{popupStoreId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPopupStore(@PathVariable Long popupStoreId) {
        List<CommentResponse> responses = commentService.getCommentsByPopupStore(popupStoreId)
                .stream().map(CommentResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
} 