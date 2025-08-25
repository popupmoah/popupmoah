package com.sgyj.popupmoah.domain.community.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 가입 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupResponse {

    private Long memberId;
    private String username;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
    private String message;
}
