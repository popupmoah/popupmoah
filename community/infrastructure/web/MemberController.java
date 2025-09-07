package com.sgyj.popupmoah.domain.community.infrastructure.web;

import com.sgyj.popupmoah.domain.community.application.dto.MemberSignupRequest;
import com.sgyj.popupmoah.domain.community.application.dto.MemberSignupResponse;
import com.sgyj.popupmoah.domain.community.application.dto.PasswordChangeRequest;
import com.sgyj.popupmoah.domain.community.application.dto.ProfileResponse;
import com.sgyj.popupmoah.domain.community.application.dto.ProfileUpdateRequest;
import com.sgyj.popupmoah.domain.community.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 회원 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "회원 관리", description = "회원 가입, 프로필 관리 API")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 가입
     */
    @Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "201", description = "회원 가입 성공"),
            @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청 데이터 또는 중복된 사용자명/이메일"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/signup")
    public ResponseEntity<MemberSignupResponse> signup(@Valid @RequestBody MemberSignupRequest request) {
        log.info("회원 가입 API 호출: username={}", request.getUsername());
        
        try {
            MemberSignupResponse response = memberService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("회원 가입 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 사용자명 중복 체크
     */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam String username) {
        log.info("사용자명 중복 체크: username={}", username);
        
        boolean exists = memberService.isUsernameExists(username);
        
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("exists", exists);
        response.put("available", !exists);
        response.put("message", exists ? "이미 사용 중인 사용자명입니다." : "사용 가능한 사용자명입니다.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 이메일 중복 체크
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        log.info("이메일 중복 체크: email={}", email);
        
        boolean exists = memberService.isEmailExists(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("exists", exists);
        response.put("available", !exists);
        response.put("message", exists ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 프로필 조회
     */
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(@RequestParam String username) {
        log.info("프로필 조회 API 호출: username={}", username);
        
        try {
            ProfileResponse response = memberService.getProfile(username);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("프로필 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 프로필 수정
     */
    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(@RequestParam String username, 
                                                       @Valid @RequestBody ProfileUpdateRequest request) {
        log.info("프로필 수정 API 호출: username={}", username);
        
        try {
            ProfileResponse response = memberService.updateProfile(username, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("프로필 수정 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 비밀번호 변경
     */
    @PutMapping("/password")
    public ResponseEntity<Object> changePassword(@RequestParam String username, 
                                               @Valid @RequestBody PasswordChangeRequest request) {
        log.info("비밀번호 변경 API 호출: username={}", username);
        
        try {
            memberService.changePassword(username, request);
            return ResponseEntity.ok(Map.of(
                "message", "비밀번호가 성공적으로 변경되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.warn("비밀번호 변경 실패: {}", e.getMessage());
            throw e;
        }
    }
}
