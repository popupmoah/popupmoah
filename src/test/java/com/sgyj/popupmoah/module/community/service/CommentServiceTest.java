package com.sgyj.popupmoah.module.community.service;

import com.sgyj.popupmoah.module.community.entity.Comment;
import com.sgyj.popupmoah.module.community.repository.CommentRepository;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PopupStoreRepository popupStoreRepository;
    @InjectMocks
    private CommentService commentService;

    private PopupStore popupStore;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        popupStore = PopupStore.builder().id(1L).build();
        comment = Comment.builder()
                .id(1L)
                .popupStore(popupStore)
                .author("작성자")
                .content("댓글 내용")
                .build();
    }

    @Test
    void 댓글_작성_성공() {
        when(popupStoreRepository.findById(1L)).thenReturn(Optional.of(popupStore));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        Comment saved = commentService.createComment(1L, "작성자", "댓글 내용");
        assertThat(saved.getAuthor()).isEqualTo("작성자");
        assertThat(saved.getContent()).isEqualTo("댓글 내용");
    }

    @Test
    void 댓글_작성_실패_존재하지_않는_팝업() {
        when(popupStoreRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> commentService.createComment(1L, "작성자", "댓글 내용"));
    }

    @Test
    void 댓글_수정_성공() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        Comment updated = commentService.updateComment(1L, "수정된 내용");
        assertThat(updated.getContent()).isEqualTo("수정된 내용");
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    void 댓글_수정_실패_존재하지_않는_댓글() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> commentService.updateComment(1L, "수정된 내용"));
    }

    @Test
    void 댓글_삭제_성공() {
        doNothing().when(commentRepository).deleteById(1L);
        commentService.deleteComment(1L);
        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    void 팝업별_댓글_목록_조회() {
        when(commentRepository.findByPopupStoreId(1L)).thenReturn(Arrays.asList(comment));
        List<Comment> comments = commentService.getCommentsByPopupStore(1L);
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getAuthor()).isEqualTo("작성자");
    }
} 