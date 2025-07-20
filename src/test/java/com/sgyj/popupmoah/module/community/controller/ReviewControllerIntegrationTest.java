package com.sgyj.popupmoah.module.community.controller;

import com.sgyj.popupmoah.config.JwtUtil;
import com.sgyj.popupmoah.module.community.entity.Member;
import com.sgyj.popupmoah.module.community.entity.Review;
import com.sgyj.popupmoah.module.community.repository.MemberRepository;
import com.sgyj.popupmoah.module.community.repository.ReviewRepository;
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
class ReviewControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired JwtUtil jwtUtil;
    @Autowired MemberRepository memberRepository;
    @Autowired ReviewRepository reviewRepository;

    String token;
    Member member;

    @BeforeEach
    void setup() {
        member = memberRepository.save(Member.builder().username("reviewer").password("pw").build());
        token = jwtUtil.generateToken(member.getUsername());
    }

    @Test
    @DisplayName("후기 작성 성공")
    void create_review_success() throws Exception {
        mockMvc.perform(post("/api/reviews")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"popupStoreId\":1," +
                        "\"content\":\"후기 내용\"," +
                        "\"rating\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("reviewer"));
    }

    @Test
    @DisplayName("후기 수정/삭제 권한 체크")
    void update_delete_review_permission() throws Exception {
        // 후기 작성
        Review review = reviewRepository.save(Review.builder()
                .popupStore(null).author(member.getUsername()).content("내용").rating(5).build());
        // 다른 사용자 토큰
        Member other = memberRepository.save(Member.builder().username("other").password("pw").build());
        String otherToken = jwtUtil.generateToken(other.getUsername());
        // 수정 - 권한 없음
        mockMvc.perform(put("/api/reviews/" + review.getId())
                .header("Authorization", "Bearer " + otherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"수정\",\"rating\":4}"))
                .andExpect(status().isForbidden());
        // 삭제 - 권한 없음
        mockMvc.perform(delete("/api/reviews/" + review.getId())
                .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden());
    }
} 