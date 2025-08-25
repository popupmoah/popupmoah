package com.sgyj.popupmoah.domain.community.infrastructure.web;

import com.sgyj.popupmoah.domain.community.application.dto.LoginRequest;
import com.sgyj.popupmoah.domain.community.application.dto.LoginResponse;
import com.sgyj.popupmoah.domain.community.application.dto.TokenRefreshRequest;
import com.sgyj.popupmoah.domain.community.application.dto.TokenRefreshResponse;
import com.sgyj.popupmoah.domain.community.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 인증 관련 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("로그인 API 호출: username={}", request.getUsername());
        
        try {
            LoginResponse response = memberService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 토큰 검증
     */
    @GetMapping("/validate")
    public ResponseEntity<Object> validateToken(@RequestHeader("Authorization") String token) {
        log.info("토큰 검증 API 호출");
        
        try {
            // "Bearer " 접두사 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 토큰 검증 로직은 JWT 필터에서 처리되므로 여기서는 성공 응답만 반환
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "message", "유효한 토큰입니다."
            ));
        } catch (Exception e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "message", "유효하지 않은 토큰입니다."
            ));
        }
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        log.info("토큰 갱신 API 호출");
        
        try {
            TokenRefreshResponse response = memberService.refreshToken(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("토큰 갱신 실패: {}", e.getMessage());
            throw e;
        }
    }
}
