package com.sgyj.popupmoah.domain.community.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 토큰 갱신 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {

    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
}



