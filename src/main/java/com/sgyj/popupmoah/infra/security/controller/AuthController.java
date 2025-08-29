package com.sgyj.popupmoah.infra.security.controller;

import com.sgyj.popupmoah.infra.security.dto.LoginRequest;
import com.sgyj.popupmoah.infra.security.dto.LoginResponse;
import com.sgyj.popupmoah.infra.security.dto.TokenRefreshRequest;
import com.sgyj.popupmoah.infra.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("로그인 요청: {}", loginRequest.getEmail());
        
        LoginResponse response = authService.login(loginRequest);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest refreshRequest) {
        log.info("토큰 갱신 요청");
        
        LoginResponse response = authService.refreshToken(refreshRequest);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        log.info("로그아웃 요청");
        
        // Bearer 접두사 제거
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        authService.logout(token);
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * 토큰 유효성 검증
     */
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        log.info("토큰 유효성 검증 요청");
        
        // Bearer 접두사 제거
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 토큰 유효성 검증 로직은 JwtAuthenticationFilter에서 처리됨
        return ResponseEntity.ok(true);
    }
}
