package com.sgyj.popupmoah.domain.community.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 토큰 갱신 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResponse {

    private String accessToken;
    private String refreshToken;
    private final String tokenType = "Bearer";
    private Long expiresIn;
    private String message;
}



