package com.sgyj.popupmoah.community.domain.port;

import com.sgyj.popupmoah.community.domain.entity.Comment;

import java.util.List;
import java.util.Optional;

/**
 * 댓글 리포지토리 포트
 * 도메인이 정의하는 인터페이스로, 인프라스트럭처가 구현
 */
public interface CommentRepositoryPort {
    
    /**
     * 댓글을 저장합니다.
     */
    Comment save(Comment comment);
    
    /**
     * ID로 댓글을 조회합니다.
     */
    Optional<Comment> findById(Long id);
    
    /**
     * 모든 댓글을 조회합니다.
     */
    List<Comment> findAll();
    
    /**
     * 팝업스토어별 댓글을 조회합니다.
     */
    List<Comment> findByPopupStoreId(Long popupStoreId);
    
    /**
     * 멤버별 댓글을 조회합니다.
     */
    List<Comment> findByMemberId(Long memberId);
    
    /**
     * 부모 댓글별 자식 댓글을 조회합니다.
     */
    List<Comment> findByParentId(Long parentId);
    
    /**
     * 댓글을 삭제합니다.
     */
    void deleteById(Long id);
    
    /**
     * 댓글이 존재하는지 확인합니다.
     */
    boolean existsById(Long id);
} 