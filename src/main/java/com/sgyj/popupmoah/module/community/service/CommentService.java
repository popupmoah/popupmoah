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
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPopupStore(Long popupStoreId) {
        return commentRepository.findByPopupStoreId(popupStoreId);
    }
} 