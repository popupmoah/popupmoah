package com.sgyj.popupmoah.domain.community.infrastructure.web;

import com.sgyj.popupmoah.domain.community.application.dto.CommentCreateRequest;
import com.sgyj.popupmoah.domain.community.application.dto.CommentResponse;
import com.sgyj.popupmoah.domain.community.application.dto.CommentUpdateRequest;
import com.sgyj.popupmoah.domain.community.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 댓글 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성
     */
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentCreateRequest request) {
        log.info("댓글 생성 API 호출: popupStoreId={}, parentId={}", request.getPopupStoreId(), request.getParentId());

        try {
            Long memberId = getCurrentMemberId();
            CommentResponse response = commentService.createComment(memberId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("댓글 생성 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request) {
        log.info("댓글 수정 API 호출: commentId={}", commentId);

        try {
            Long memberId = getCurrentMemberId();
            CommentResponse response = commentService.updateComment(commentId, memberId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("댓글 수정 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long commentId) {
        log.info("댓글 삭제 API 호출: commentId={}", commentId);

        try {
            Long memberId = getCurrentMemberId();
            commentService.deleteComment(commentId, memberId);
            return ResponseEntity.ok(Map.of(
                "message", "댓글이 성공적으로 삭제되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.warn("댓글 삭제 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 팝업스토어별 댓글 목록 조회
     */
    @GetMapping("/popupstore/{popupStoreId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPopupStore(@PathVariable Long popupStoreId) {
        log.info("팝업스토어 댓글 목록 조회 API 호출: popupStoreId={}", popupStoreId);

        try {
            List<CommentResponse> comments = commentService.getCommentsByPopupStore(popupStoreId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("팝업스토어 댓글 목록 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 회원별 댓글 목록 조회
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByMember(@PathVariable Long memberId) {
        log.info("회원 댓글 목록 조회 API 호출: memberId={}", memberId);

        try {
            List<CommentResponse> comments = commentService.getCommentsByMember(memberId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("회원 댓글 목록 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 댓글 상세 조회
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long commentId) {
        log.info("댓글 상세 조회 API 호출: commentId={}", commentId);

        try {
            CommentResponse comment = commentService.getComment(commentId);
            return ResponseEntity.ok(comment);
        } catch (IllegalArgumentException e) {
            log.warn("댓글 상세 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 팝업스토어 댓글 개수 조회
     */
    @GetMapping("/popupstore/{popupStoreId}/count")
    public ResponseEntity<Object> getCommentCount(@PathVariable Long popupStoreId) {
        log.info("팝업스토어 댓글 개수 조회 API 호출: popupStoreId={}", popupStoreId);

        try {
            Long commentCount = commentService.getCommentCount(popupStoreId);
            return ResponseEntity.ok(Map.of(
                "popupStoreId", popupStoreId,
                "commentCount", commentCount
            ));
        } catch (Exception e) {
            log.error("팝업스토어 댓글 개수 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 현재 인증된 사용자 ID 가져오기 (임시 구현)
     */
    private Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        
        // 실제 구현에서는 JWT 토큰에서 memberId를 추출하거나
        // 사용자명으로 회원 정보를 조회하여 ID를 반환
        // 현재는 임시로 1을 반환
        return 1L; // TODO: 실제 memberId 추출 로직 구현
    }
}
