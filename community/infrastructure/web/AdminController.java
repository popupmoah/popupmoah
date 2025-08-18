package com.sgyj.popupmoah.domain.community.infrastructure.web;

import com.sgyj.popupmoah.domain.community.application.dto.MemberListResponse;
import com.sgyj.popupmoah.domain.community.application.dto.MemberRoleUpdateRequest;
import com.sgyj.popupmoah.domain.community.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 관리자 기능 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final MemberService memberService;

    /**
     * 회원 목록 조회
     */
    @GetMapping
    public ResponseEntity<MemberListResponse> getMemberList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("회원 목록 조회 API 호출: page={}, size={}", page, size);
        
        try {
            MemberListResponse response = memberService.getMemberList(page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("회원 목록 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 회원 상세 정보 조회
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<Object> getMemberDetail(@PathVariable Long memberId) {
        log.info("회원 상세 정보 조회 API 호출: memberId={}", memberId);
        
        try {
            var member = memberService.findById(memberId);
            return ResponseEntity.ok(Map.of(
                "memberId", member.getId(),
                "username", member.getUsername(),
                "email", member.getEmail(),
                "nickname", member.getNickname(),
                "profileImageUrl", member.getProfileImageUrl(),
                "role", member.getRole().name(),
                "active", member.isActive(),
                "createdAt", member.getCreatedAt(),
                "updatedAt", member.getUpdatedAt()
            ));
        } catch (IllegalArgumentException e) {
            log.warn("회원 상세 정보 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 회원 권한 변경
     */
    @PutMapping("/{memberId}/role")
    public ResponseEntity<Object> updateMemberRole(
            @PathVariable Long memberId,
            @Valid @RequestBody MemberRoleUpdateRequest request) {
        log.info("회원 권한 변경 API 호출: memberId={}, newRole={}", memberId, request.getRole());
        
        try {
            memberService.updateMemberRole(memberId, request);
            return ResponseEntity.ok(Map.of(
                "message", "회원 권한이 성공적으로 변경되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.warn("회원 권한 변경 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 회원 활성화/비활성화
     */
    @PutMapping("/{memberId}/status")
    public ResponseEntity<Object> toggleMemberStatus(@PathVariable Long memberId) {
        log.info("회원 상태 변경 API 호출: memberId={}", memberId);
        
        try {
            memberService.toggleMemberStatus(memberId);
            return ResponseEntity.ok(Map.of(
                "message", "회원 상태가 성공적으로 변경되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.warn("회원 상태 변경 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 회원 삭제
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Object> deleteMember(@PathVariable Long memberId) {
        log.info("회원 삭제 API 호출: memberId={}", memberId);
        
        try {
            memberService.deleteMember(memberId);
            return ResponseEntity.ok(Map.of(
                "message", "회원이 성공적으로 삭제되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.warn("회원 삭제 실패: {}", e.getMessage());
            throw e;
        }
    }
}



