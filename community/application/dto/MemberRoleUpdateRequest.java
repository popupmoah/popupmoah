package com.sgyj.popupmoah.domain.community.application.dto;

import com.sgyj.popupmoah.domain.community.entity.MemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원 권한 변경 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRoleUpdateRequest {

    @NotNull(message = "권한은 필수입니다.")
    private MemberRole role;
}
