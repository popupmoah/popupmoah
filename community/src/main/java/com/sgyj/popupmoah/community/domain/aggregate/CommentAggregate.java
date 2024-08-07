package com.sgyj.popupmoah.community.domain.aggregate;

import com.sgyj.popupmoah.community.domain.entity.Comment;
import com.sgyj.popupmoah.community.domain.port.CommentRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * 댓글 어그리게이트 루트
 * 댓글 도메인에 대한 접근을 제어하는 루트 엔티티
 */
public class CommentAggregate {
    
    private final CommentRepositoryPort repository;
    
    public CommentAggregate(CommentRepositoryPort repository) {
        this.repository = repository;
    }
    
    /**
     * 댓글을 생성합니다.
     */
    public Comment create(Comment comment) {
        // 도메인 규칙 검증
        validateComment(comment);
        return repository.save(comment);
    }
    
    /**
     * 댓글을 조회합니다.
     */
    public Optional<Comment> findById(Long id) {
        return repository.findById(id);
    }
    
    /**
     * 모든 댓글을 조회합니다.
     */
    public List<Comment> findAll() {
        return repository.findAll();
    }
    
    /**
     * 팝업스토어별 댓글을 조회합니다.
     */
    public List<Comment> findByPopupStoreId(Long popupStoreId) {
        return repository.findByPopupStoreId(popupStoreId);
    }
    
    /**
     * 멤버별 댓글을 조회합니다.
     */
    public List<Comment> findByMemberId(Long memberId) {
        return repository.findByMemberId(memberId);
    }
    
    /**
     * 부모 댓글별 자식 댓글을 조회합니다.
     */
    public List<Comment> findByParentId(Long parentId) {
        return repository.findByParentId(parentId);
    }
    
    /**
     * 댓글을 업데이트합니다.
     */
    public Comment update(Long id, Comment comment) {
        Optional<Comment> existing = repository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다: " + id);
        }
        
        // 도메인 규칙 검증
        validateComment(comment);
        
        Comment updated = existing.get();
        // 불변성을 유지하면서 업데이트
        updated = Comment.builder()
                .id(updated.getId())
                .popupStore(comment.getPopupStore())
                .member(comment.getMember())
                .parent(comment.getParent())
                .content(comment.getContent())
                .deleted(comment.getDeleted())
                .build();
        
        return repository.save(updated);
    }
    
    /**
     * 댓글을 삭제합니다.
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다: " + id);
        }
        repository.deleteById(id);
    }
    
    /**
     * 댓글을 소프트 삭제합니다.
     */
    public void softDelete(Long id) {
        Optional<Comment> comment = repository.findById(id);
        if (comment.isPresent()) {
            Comment com = comment.get();
            com.softDelete();
            repository.save(com);
        }
    }
    
    /**
     * 댓글 도메인 규칙을 검증합니다.
     */
    private void validateComment(Comment comment) {
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 필수입니다.");
        }
        
        if (comment.getPopupStore() == null) {
            throw new IllegalArgumentException("팝업스토어는 필수입니다.");
        }
        
        if (comment.getMember() == null) {
            throw new IllegalArgumentException("멤버는 필수입니다.");
        }
    }
} 