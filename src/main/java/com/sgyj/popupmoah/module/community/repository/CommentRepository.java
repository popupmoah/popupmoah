package com.sgyj.popupmoah.module.community.repository;

import com.sgyj.popupmoah.module.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPopupStoreId(Long popupStoreId);
} 