package com.sgyj.popupmoah.module.community.service;

import com.sgyj.popupmoah.module.community.entity.Comment;
import com.sgyj.popupmoah.module.community.repository.CommentRepository;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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
        List<Comment> parents = commentRepository.findByPopupStoreIdAndParentIsNull(popupStoreId);
        for (Comment parent : parents) {
            loadChildrenRecursive(parent);
        }
        return parents;
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
        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글입니다."));
        Comment reply = Comment.builder()
                .popupStore(popupStore)
                .parent(parent)
                .author(author)
                .content(content)
                .build();
        return commentRepository.save(reply);
    }
} 