package com.sgyj.popupmoah.module.community.repository;

import com.sgyj.popupmoah.module.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c where c.popupStore.id = :popupStoreId and c.deleted = false")
    List<Comment> findByPopupStoreId(Long popupStoreId);
    @Query("select c from Comment c where c.popupStore.id = :popupStoreId and c.parent is null and c.deleted = false")
    List<Comment> findByPopupStoreIdAndParentIsNull(Long popupStoreId);
    @Query("select c from Comment c where c.parent.id = :parentId and c.deleted = false")
    List<Comment> findByParentId(Long parentId);
    @Query("select c from Comment c where c.popupStore.id = :popupStoreId and c.deleted = false")
    List<Comment> findAllByPopupStoreId(Long popupStoreId);
    @Query("select c from Comment c where c.popupStore.id = :popupStoreId and c.parent is null and c.deleted = false")
    Page<Comment> findByPopupStoreIdAndParentIsNull(Long popupStoreId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Comment c where c.id = :id")
    Optional<Comment> findByIdForUpdate(Long id);
} 