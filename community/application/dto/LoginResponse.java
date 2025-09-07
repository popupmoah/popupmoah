package com.sgyj.popupmoah.domain.community.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 로그인 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private final String tokenType = "Bearer";
    private Long expiresIn;
    private Long memberId;
    private String username;
    private String nickname;
    private String role;
    private String message;
}



