package com.sgyj.popupmoah.domain.community.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 프로필 수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @Size(max = 50, message = "닉네임은 50자 이하여야 합니다.")
    private String nickname;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Size(max = 500, message = "프로필 이미지 URL은 500자 이하여야 합니다.")
    private String profileImageUrl;
}



