package com.sgyj.popupmoah.module.community.controller;

import com.sgyj.popupmoah.config.JwtUtil;
import com.sgyj.popupmoah.module.community.entity.Member;
import com.sgyj.popupmoah.module.community.entity.Comment;
import com.sgyj.popupmoah.module.community.repository.MemberRepository;
import com.sgyj.popupmoah.module.community.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired JwtUtil jwtUtil;
    @Autowired MemberRepository memberRepository;
    @Autowired CommentRepository commentRepository;

    String token;
    Member member;

    @BeforeEach
    void setup() {
        member = memberRepository.save(Member.builder().username("commenter").password("pw").build());
        token = jwtUtil.generateToken(member.getUsername());
    }

    @Test
    @DisplayName("댓글 작성 성공")
    void create_comment_success() throws Exception {
        mockMvc.perform(post("/api/comments")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"popupStoreId\":1," +
                        "\"content\":\"댓글 내용\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("commenter"));
    }

    @Test
    @DisplayName("댓글 수정/삭제 권한 체크")
    void update_delete_comment_permission() throws Exception {
        // 댓글 작성
        Comment comment = commentRepository.save(Comment.builder()
                .popupStore(null).author(member.getUsername()).content("내용").build());
        // 다른 사용자 토큰
        Member other = memberRepository.save(Member.builder().username("other").password("pw").build());
        String otherToken = jwtUtil.generateToken(other.getUsername());
        // 수정 - 권한 없음
        mockMvc.perform(put("/api/comments/" + comment.getId())
                .header("Authorization", "Bearer " + otherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"수정\"}"))
                .andExpect(status().isForbidden());
        // 삭제 - 권한 없음
        mockMvc.perform(delete("/api/comments/" + comment.getId())
                .header("Authorization", "Bearer " + otherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("댓글 soft delete 후 조회시 '삭제된 댓글입니다' 표시")
    void soft_delete_comment() throws Exception {
        // 댓글 작성
        Comment comment = commentRepository.save(Comment.builder()
                .popupStore(null).author(member.getUsername()).content("내용").build());
        // 삭제
        mockMvc.perform(delete("/api/comments/" + comment.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNoContent());
        // 조회
        mockMvc.perform(get("/api/comments/popup/1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("삭제된 댓글입니다"));
    }
} 