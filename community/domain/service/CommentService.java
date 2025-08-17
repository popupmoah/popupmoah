package com.sgyj.popupmoah.domain.community.service;

import com.sgyj.popupmoah.domain.community.application.dto.CommentCreateRequest;
import com.sgyj.popupmoah.domain.community.application.dto.CommentResponse;
import com.sgyj.popupmoah.domain.community.application.dto.CommentUpdateRequest;
import com.sgyj.popupmoah.domain.community.entity.Comment;
import com.sgyj.popupmoah.domain.community.repository.CommentRepository;
import com.sgyj.popupmoah.domain.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.domain.popupstore.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 댓글 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PopupStoreRepository popupStoreRepository;
    private final MemberService memberService;

    /**
     * 댓글 생성
     */
    @Transactional
    public CommentResponse createComment(Long memberId, CommentCreateRequest request) {
        log.info("댓글 생성 요청: memberId={}, popupStoreId={}", memberId, request.getPopupStoreId());

        // 팝업스토어 존재 확인
        PopupStore popupStore = popupStoreRepository.findById(request.getPopupStoreId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팝업스토어입니다."));

        // 회원 존재 확인
        var member = memberService.findById(memberId);

        // 부모 댓글 확인 (대댓글인 경우)
        Comment parentComment = null;
        if (request.getParentId() != null) {
            parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글입니다."));
            
            // 부모 댓글이 같은 팝업스토어의 댓글인지 확인
            if (!parentComment.getPopupStore().getId().equals(request.getPopupStoreId())) {
                throw new IllegalArgumentException("부모 댓글과 다른 팝업스토어입니다.");
            }
        }

        // 댓글 생성
        Comment comment = Comment.builder()
                .popupStore(popupStore)
                .member(member)
                .parent(parentComment)
                .content(request.getContent())
                .deleted(false)
                .build();

        Comment savedComment = commentRepository.save(comment);

        log.info("댓글 생성 완료: commentId={}, memberId={}, popupStoreId={}", 
                savedComment.getId(), memberId, request.getPopupStoreId());

        return convertToResponse(savedComment);
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CommentResponse updateComment(Long commentId, Long memberId, CommentUpdateRequest request) {
        log.info("댓글 수정 요청: commentId={}, memberId={}", commentId, memberId);

        Comment comment = findById(commentId);

        // 권한 확인 (본인이 작성한 댓글만 수정 가능)
        if (!comment.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }

        // 삭제된 댓글인지 확인
        if (comment.isDeleted()) {
            throw new IllegalArgumentException("삭제된 댓글은 수정할 수 없습니다.");
        }

        // 댓글 수정
        comment.updateContent(request.getContent());

        log.info("댓글 수정 완료: commentId={}, memberId={}", commentId, memberId);

        return convertToResponse(comment);
    }

    /**
     * 댓글 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        log.info("댓글 삭제 요청: commentId={}, memberId={}", commentId, memberId);

        Comment comment = findById(commentId);

        // 권한 확인 (본인이 작성한 댓글만 삭제 가능)
        if (!comment.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        // 이미 삭제된 댓글인지 확인
        if (comment.isDeleted()) {
            throw new IllegalArgumentException("이미 삭제된 댓글입니다.");
        }

        // 소프트 삭제
        comment.softDelete();

        log.info("댓글 삭제 완료: commentId={}, memberId={}", commentId, memberId);
    }

    /**
     * 팝업스토어별 댓글 목록 조회 (계층 구조)
     */
    public List<CommentResponse> getCommentsByPopupStore(Long popupStoreId) {
        log.info("팝업스토어 댓글 목록 조회: popupStoreId={}", popupStoreId);

        // 최상위 댓글만 조회 (부모가 없는 댓글)
        List<Comment> topLevelComments = commentRepository.findByPopupStoreIdAndParentIsNull(popupStoreId);
        
        return topLevelComments.stream()
                .filter(comment -> !comment.isDeleted())
                .map(this::convertToResponseWithReplies)
                .collect(Collectors.toList());
    }

    /**
     * 회원별 댓글 목록 조회
     */
    public List<CommentResponse> getCommentsByMember(Long memberId) {
        log.info("회원 댓글 목록 조회: memberId={}", memberId);

        List<Comment> comments = commentRepository.findByMemberId(memberId);
        
        return comments.stream()
                .filter(comment -> !comment.isDeleted())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 상세 조회
     */
    public CommentResponse getComment(Long commentId) {
        log.info("댓글 상세 조회: commentId={}", commentId);

        Comment comment = findById(commentId);
        
        if (comment.isDeleted()) {
            throw new IllegalArgumentException("삭제된 댓글입니다.");
        }
        
        return convertToResponseWithReplies(comment);
    }

    /**
     * 팝업스토어 댓글 개수 조회
     */
    public Long getCommentCount(Long popupStoreId) {
        return commentRepository.getCommentCountByPopupStoreId(popupStoreId);
    }

    /**
     * 댓글 ID로 조회
     */
    private Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
    }

    /**
     * Comment 엔티티를 CommentResponse로 변환 (대댓글 포함)
     */
    private CommentResponse convertToResponseWithReplies(Comment comment) {
        CommentResponse response = convertToResponse(comment);
        
        // 대댓글 조회
        List<Comment> replies = commentRepository.findByParentId(comment.getId());
        List<CommentResponse> replyResponses = replies.stream()
                .filter(reply -> !reply.isDeleted())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        response.setReplies(replyResponses);
        response.setReplyCount(replyResponses.size());
        
        return response;
    }

    /**
     * Comment 엔티티를 CommentResponse로 변환
     */
    private CommentResponse convertToResponse(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .popupStoreId(comment.getPopupStore().getId())
                .memberId(comment.getMember().getId())
                .memberUsername(comment.getMember().getUsername())
                .memberNickname(comment.getMember().getNickname())
                .content(comment.getContent())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replies(List.of())
                .replyCount(0)
                .build();
    }
}
