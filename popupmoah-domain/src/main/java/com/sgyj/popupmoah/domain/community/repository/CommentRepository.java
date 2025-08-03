package com.sgyj.popupmoah.domain.community.repository;

import com.sgyj.popupmoah.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByPopupStoreIdAndParentIsNull(Long popupStoreId);
    
    List<Comment> findByParentId(Long parentId);
    
    List<Comment> findByMemberId(Long memberId);
} 