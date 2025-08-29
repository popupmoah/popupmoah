package com.sgyj.popupmoah.infra.security.service;

import com.sgyj.popupmoah.infra.security.JwtTokenProvider;
import com.sgyj.popupmoah.infra.security.dto.LoginRequest;
import com.sgyj.popupmoah.infra.security.dto.LoginResponse;
import com.sgyj.popupmoah.infra.security.dto.TokenRefreshRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
    /**
     * 로그인을 처리하고 JWT 토큰을 반환합니다.
     */
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("로그인 시도: {}", loginRequest.getEmail());
        
        // 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        // JWT 토큰 생성
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails.getUsername());
        
        // 권한 정보 추출
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        log.info("로그인 성공: {}", loginRequest.getEmail());
        
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationDateFromToken(accessToken).getTime() - System.currentTimeMillis())
                .email(userDetails.getUsername())
                .role(role)
                .build();
    }
    
    /**
     * 리프레시 토큰으로 새로운 액세스 토큰을 발급합니다.
     */
    public LoginResponse refreshToken(TokenRefreshRequest refreshRequest) {
        log.info("토큰 갱신 요청");
        
        String refreshToken = refreshRequest.getRefreshToken();
        
        // 리프레시 토큰 유효성 검증
        if (!tokenProvider.validateToken(refreshToken) || !tokenProvider.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
        
        // 사용자명 추출
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        
        // 새로운 액세스 토큰 생성
        String newAccessToken = tokenProvider.generateToken(username, "ROLE_USER");
        
        log.info("토큰 갱신 성공: {}", username);
        
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // 리프레시 토큰은 그대로 유지
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationDateFromToken(newAccessToken).getTime() - System.currentTimeMillis())
                .email(username)
                .role("ROLE_USER")
                .build();
    }
    
    /**
     * 로그아웃을 처리합니다.
     */
    public void logout(String token) {
        log.info("로그아웃 처리");
        // JWT는 stateless이므로 서버 측에서 별도 처리 불필요
        // 클라이언트에서 토큰을 삭제하면 됨
        // 필요시 블랙리스트 기능 추가 가능
    }
}
