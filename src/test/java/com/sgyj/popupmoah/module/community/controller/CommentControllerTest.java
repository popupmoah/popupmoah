package com.sgyj.popupmoah.module.community.controller;

import com.sgyj.popupmoah.module.community.entity.Comment;
import com.sgyj.popupmoah.module.community.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@Import(CommentControllerTest.MockServiceConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private CommentService commentService;

    @TestConfiguration
    static class MockServiceConfig {
        @Bean
        public CommentService commentService() {
            return org.mockito.Mockito.mock(CommentService.class);
        }
    }

    @Test
    @DisplayName("댓글/대댓글 트리 조회 API 동작 테스트")
    void 댓글_트리_조회_API() throws Exception {
        // 부모 댓글
        Comment parent = Comment.builder().id(1L).author("부모").content("부모댓글").build();
        // 대댓글
        Comment child = Comment.builder().id(2L).author("자식").content("대댓글").parent(parent).build();
        Mockito.when(commentService.getCommentTreeByPopupStore(anyLong())).thenReturn(java.util.Collections.singletonList(parent));
        Mockito.when(commentService.getRepliesByParent(1L)).thenReturn(java.util.Collections.singletonList(child));
        Mockito.when(commentService.getRepliesByParent(2L)).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/comments/popup/1/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].author").value("부모"))
                .andExpect(jsonPath("$[0].children[0].id").value(2L))
                .andExpect(jsonPath("$[0].children[0].author").value("자식"));
    }

    @Test
    @DisplayName("댓글 작성 성공")
    void 댓글_작성_성공() throws Exception {
        Comment comment = Comment.builder().id(1L).author("작성자").content("내용").build();
        Mockito.when(commentService.createComment(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString())).thenReturn(comment);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/comments")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"popupStoreId\":1,\"author\":\"작성자\",\"content\":\"내용\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.author").value("작성자"));
    }

    @Test
    @DisplayName("댓글 작성 실패 - 팝업스토어 없음")
    void 댓글_작성_실패_팝업없음() throws Exception {
        Mockito.when(commentService.createComment(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new IllegalArgumentException("존재하지 않는 팝업스토어입니다."));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/comments")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"popupStoreId\":1,\"author\":\"작성자\",\"content\":\"내용\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("대댓글 작성 성공")
    void 대댓글_작성_성공() throws Exception {
        Comment parent = Comment.builder().id(1L).build();
        Comment reply = Comment.builder().id(2L).author("답글").content("대댓글").parent(parent).build();
        Mockito.when(commentService.createReply(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyString())).thenReturn(reply);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/comments/reply")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"popupStoreId\":1,\"parentCommentId\":1,\"author\":\"답글\",\"content\":\"대댓글\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.author").value("답글"));
    }

    @Test
    @DisplayName("대댓글 작성 실패 - 부모 댓글 없음")
    void 대댓글_작성_실패_부모없음() throws Exception {
        Mockito.when(commentService.createReply(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new IllegalArgumentException("존재하지 않는 부모 댓글입니다."));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/comments/reply")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"popupStoreId\":1,\"parentCommentId\":1,\"author\":\"답글\",\"content\":\"대댓글\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 수정 성공 - 본인")
    void 댓글_수정_성공_본인() throws Exception {
        Comment comment = Comment.builder().id(1L).author("작성자").content("수정된 내용").build();
        Mockito.when(commentService.updateComment(Mockito.eq(1L), Mockito.eq("수정된 내용"), Mockito.eq("작성자"))).thenReturn(comment);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/comments/1")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"content\":\"수정된 내용\",\"currentUser\":\"작성자\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("수정된 내용"));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 권한 없음")
    void 댓글_수정_실패_권한없음() throws Exception {
        Mockito.when(commentService.updateComment(Mockito.eq(1L), Mockito.eq("수정된 내용"), Mockito.eq("다른사람")))
                .thenThrow(new SecurityException("수정 권한이 없습니다."));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/comments/1")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"content\":\"수정된 내용\",\"currentUser\":\"다른사람\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("댓글 삭제 성공 - 본인")
    void 댓글_삭제_성공_본인() throws Exception {
        Mockito.doNothing().when(commentService).deleteComment(Mockito.eq(1L), Mockito.eq("작성자"));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/comments/1")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"currentUser\":\"작성자\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 권한 없음")
    void 댓글_삭제_실패_권한없음() throws Exception {
        Mockito.doThrow(new SecurityException("삭제 권한이 없습니다.")).when(commentService).deleteComment(Mockito.eq(1L), Mockito.eq("다른사람"));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/comments/1")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"currentUser\":\"다른사람\"}"))
                .andExpect(status().isForbidden());
    }
} 