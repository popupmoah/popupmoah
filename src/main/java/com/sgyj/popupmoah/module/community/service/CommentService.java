package com.sgyj.popupmoah.module.community.service;

import com.sgyj.popupmoah.module.community.entity.Comment;
import com.sgyj.popupmoah.module.community.repository.CommentRepository;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PopupStoreRepository popupStoreRepository;

    @Transactional
    public Comment createComment(Long popupStoreId, String author, String content) {
        PopupStore popupStore = popupStoreRepository.findById(popupStoreId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팝업스토어입니다."));
        Comment comment = Comment.builder()
                .popupStore(popupStore)
                .author(author)
                .content(content)
                .build();
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        comment.updateContent(newContent);
        return comment;
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        commentRepository.deleteById(commentId);
    }

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

    private void loadChildrenRecursive(Comment parent) {
        List<Comment> children = commentRepository.findByParentId(parent.getId());
        for (Comment child : children) {
            loadChildrenRecursive(child);
        }
        // 자식 댓글을 엔티티에 직접 저장하지 않고, 응답 DTO에서 처리
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
} 