package com.sgyj.popupmoah.domain.community.infrastructure.security;

import com.sgyj.popupmoah.domain.community.entity.Member;
import com.sgyj.popupmoah.domain.community.entity.MemberRole;
import com.sgyj.popupmoah.domain.community.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 권한 검증 유틸리티
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationUtils {

    private final JwtService jwtService;

    /**
     * 현재 인증된 사용자의 ID를 반환
     */
    public Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        
        // SecurityContext에서 사용자명을 가져와서 회원 ID를 조회
        // 실제 구현에서는 JWT 토큰에서 직접 memberId를 추출하거나
        // 사용자명으로 회원 정보를 조회하여 ID를 반환
        return null; // TODO: MemberService를 주입받아 사용자명으로 회원 ID 조회
    }

    /**
     * 현재 인증된 사용자의 사용자명을 반환
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        
        return authentication.getName();
    }

    /**
     * 현재 사용자가 관리자인지 확인
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * 리소스 소유자 권한 확인
     */
    public boolean isResourceOwner(Long resourceOwnerId) {
        Long currentMemberId = getCurrentMemberId();
        return currentMemberId != null && currentMemberId.equals(resourceOwnerId);
    }

    /**
     * 리뷰/댓글 수정 권한 확인
     */
    public boolean canModifyReview(Long reviewOwnerId) {
        return isResourceOwner(reviewOwnerId) || isAdmin();
    }

    /**
     * 리뷰/댓글 삭제 권한 확인
     */
    public boolean canDeleteReview(Long reviewOwnerId) {
        return isResourceOwner(reviewOwnerId) || isAdmin();
    }

    /**
     * 관리자 권한 확인
     */
    public void requireAdmin() {
        if (!isAdmin()) {
            throw new IllegalStateException("관리자 권한이 필요합니다.");
        }
    }

    /**
     * 인증 확인
     */
    public void requireAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증이 필요합니다.");
        }
    }
}
