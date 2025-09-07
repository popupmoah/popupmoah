package com.sgyj.popupmoah.domain.community.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 회원 목록 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberListResponse {

    private List<MemberSummary> members;
    private long totalCount;
    private int pageNumber;
    private int pageSize;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberSummary {
        private Long memberId;
        private String username;
        private String email;
        private String nickname;
        private String role;
        private boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}



