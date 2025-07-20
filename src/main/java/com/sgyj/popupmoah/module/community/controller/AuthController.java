package com.sgyj.popupmoah.module.community.controller;

import com.sgyj.popupmoah.config.JwtUtil;
import com.sgyj.popupmoah.module.community.entity.Member;
import com.sgyj.popupmoah.module.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 인증/회원가입/로그인/JWT 토큰 발급 관련 API 컨트롤러.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * JWT 로그인(토큰 발급) API. username, password 검증 후 JWT 반환.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        Member member = memberRepository.findByUsername(username)
                .orElse(null);
        if (member == null || !passwordEncoder.matches(password, member.getPassword())) {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        String token = jwtUtil.generateToken(username);
        return ResponseEntity.ok(token);
    }

    /**
     * 회원가입 API. username 중복 체크, 비밀번호 암호화 후 저장.
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestParam String username, @RequestParam String password) {
        if (memberRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("이미 존재하는 사용자입니다.");
        }
        String encoded = passwordEncoder.encode(password);
        memberRepository.save(Member.builder().username(username).password(encoded).build());
        return ResponseEntity.ok("회원가입 성공");
    }
} 