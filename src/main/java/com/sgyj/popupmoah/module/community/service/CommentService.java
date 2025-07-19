package com.sgyj.popupmoah.module.community.service;

import com.sgyj.popupmoah.module.community.entity.Comment;
import com.sgyj.popupmoah.module.community.repository.CommentRepository;
import com.sgyj.popupmoah.module.community.event.CommentCreatedEvent;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async; // 비동기 어노테이션
import org.springframework.context.ApplicationEventPublisher;

/**
 * 댓글(Comment) 관련 비즈니스 로직을 처리하는 서비스.
 * 작성, 수정, 삭제, 트리 조회, 권한 체크, soft delete 등 포함.
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PopupStoreRepository popupStoreRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 댓글을 작성한다.
     * @param popupStoreId 팝업스토어 ID
     * @param author 작성자
     * @param content 내용
     * @return 저장된 댓글
     */
    @Transactional
    public Comment createComment(Long popupStoreId, String author, String content) {
        PopupStore popupStore = popupStoreRepository.findById(popupStoreId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팝업스토어입니다."));
        Comment comment = Comment.builder()
                .popupStore(popupStore)
                .author(author)
                .content(content)
                .build();
        // 댓글 생성 이벤트 발행
        eventPublisher.publishEvent(new CommentCreatedEvent(comment.getId(), author, content));
        return commentRepository.save(comment);
    }

    /**
     * 댓글을 수정한다. (권한 체크)
     * @param commentId 댓글 ID
     * @param newContent 수정 내용
     * @param currentUser 현재 사용자명
     * @return 수정된 댓글
     * @throws SecurityException 권한 없음
     */
    @Transactional
    public Comment updateComment(Long commentId, String newContent, String currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        if (!comment.getAuthor().equals(currentUser)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }
        comment.updateContent(newContent);
        return comment;
    }

    /**
     * 댓글을 soft delete 한다. (권한 체크)
     * @param commentId 댓글 ID
     * @param currentUser 현재 사용자명
     * @throws SecurityException 권한 없음
     */
    @Transactional
    public void deleteComment(Long commentId, String currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        if (!comment.getAuthor().equals(currentUser)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }
        if (comment.isDeleted()) {
            throw new IllegalStateException("이미 삭제된 댓글입니다.");
        }
        comment.softDelete();
    }

    /**
     * 팝업스토어의 모든 댓글(soft delete 제외)을 조회한다.
     */
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPopupStore(Long popupStoreId) {
        return commentRepository.findByPopupStoreId(popupStoreId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getParentCommentsByPopupStore(Long popupStoreId) {
        return commentRepository.findByPopupStoreIdAndParentIsNull(popupStoreId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getRepliesByParent(Long parentCommentId) {
        return commentRepository.findByParentId(parentCommentId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentTreeByPopupStore(Long popupStoreId) {
        List<Comment> allComments = commentRepository.findAllByPopupStoreId(popupStoreId);
        // parentId -> List<Comment> 맵핑
        java.util.Map<Long, List<Comment>> parentMap = new java.util.HashMap<>();
        for (Comment c : allComments) {
            Long parentId = c.getParent() == null ? null : c.getParent().getId();
            parentMap.computeIfAbsent(parentId, k -> new java.util.ArrayList<>()).add(c);
        }
        // 최상위(부모) 댓글만 추출
        List<Comment> roots = parentMap.get(null);
        if (roots == null) return java.util.Collections.emptyList();
        // 자식 댓글을 재귀적으로 연결 (엔티티에 직접 children 필드가 없으므로, 컨트롤러에서 변환)
        return roots;
    }

    @Transactional
    public Comment createReply(Long popupStoreId, Long parentCommentId, String author, String content) {
        PopupStore popupStore = popupStoreRepository.findById(popupStoreId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팝업스토어입니다."));
        Comment parent = commentRepository.findByIdForUpdate(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글입니다."));
        Comment reply = Comment.builder()
                .popupStore(popupStore)
                .parent(parent)
                .author(author)
                .content(content)
                .build();
        return commentRepository.save(reply);
    }

    /**
     * 부모 댓글만 페이징하여 트리 구조로 반환한다.
     */
    @Transactional(readOnly = true)
    public Page<Comment> getCommentTreeByPopupStoreWithPaging(Long popupStoreId, Pageable pageable) {
        Page<Comment> parentPage = commentRepository.findByPopupStoreIdAndParentIsNull(popupStoreId, pageable);
        List<Comment> allComments = commentRepository.findAllByPopupStoreId(popupStoreId);
        java.util.Map<Long, List<Comment>> parentMap = new java.util.HashMap<>();
        for (Comment c : allComments) {
            Long parentId = c.getParent() == null ? null : c.getParent().getId();
            parentMap.computeIfAbsent(parentId, k -> new java.util.ArrayList<>()).add(c);
        }
        // 부모 댓글만 페이징, 각 부모의 자식 트리 변환은 컨트롤러에서 처리
        return parentPage;
    }

    /**
     * 댓글 등록 시 비동기로 알림 전송 (예시)
     */
    @Async
    public void sendCommentNotification(String to, String message) {
        // 실제로는 이메일, SMS, 푸시 등 외부 연동
        System.out.println("[비동기 알림] " + to + ": " + message);
        // 예시: Thread.sleep(1000); // 실제 외부 연동 대기 시
    }
} 