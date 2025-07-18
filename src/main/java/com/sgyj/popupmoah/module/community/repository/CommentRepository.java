package com.sgyj.popupmoah.module.community.repository;

import com.sgyj.popupmoah.module.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPopupStoreId(Long popupStoreId);
    List<Comment> findByPopupStoreIdAndParentIsNull(Long popupStoreId);
    List<Comment> findByParentId(Long parentId);
    List<Comment> findAllByPopupStoreId(Long popupStoreId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Comment c where c.id = :id")
    Optional<Comment> findByIdForUpdate(Long id);
} 