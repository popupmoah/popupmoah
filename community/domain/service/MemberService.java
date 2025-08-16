package com.sgyj.popupmoah.domain.community.service;

import com.sgyj.popupmoah.domain.community.application.dto.LoginRequest;
import com.sgyj.popupmoah.domain.community.application.dto.LoginResponse;
import com.sgyj.popupmoah.domain.community.application.dto.MemberListResponse;
import com.sgyj.popupmoah.domain.community.application.dto.MemberRoleUpdateRequest;
import com.sgyj.popupmoah.domain.community.application.dto.MemberSignupRequest;
import com.sgyj.popupmoah.domain.community.application.dto.MemberSignupResponse;
import com.sgyj.popupmoah.domain.community.application.dto.PasswordChangeRequest;
import com.sgyj.popupmoah.domain.community.application.dto.ProfileResponse;
import com.sgyj.popupmoah.domain.community.application.dto.ProfileUpdateRequest;
import com.sgyj.popupmoah.domain.community.application.dto.TokenRefreshRequest;
import com.sgyj.popupmoah.domain.community.application.dto.TokenRefreshResponse;
import com.sgyj.popupmoah.domain.community.entity.Member;
import com.sgyj.popupmoah.domain.community.entity.MemberRole;
import com.sgyj.popupmoah.domain.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * 회원 가입
     */
    @Transactional
    public MemberSignupResponse signup(MemberSignupRequest request) {
        log.info("회원 가입 요청: username={}, email={}", request.getUsername(), request.getEmail());

        // 중복 사용자명 체크
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");
        }

        // 중복 이메일 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 회원 생성
        Member member = Member.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .email(request.getEmail())
                .nickname(request.getNickname() != null ? request.getNickname() : request.getUsername())
                .role(MemberRole.USER)
                .active(true)
                .build();

        Member savedMember = memberRepository.save(member);

        log.info("회원 가입 완료: memberId={}, username={}", savedMember.getId(), savedMember.getUsername());

        return MemberSignupResponse.builder()
                .memberId(savedMember.getId())
                .username(savedMember.getUsername())
                .email(savedMember.getEmail())
                .nickname(savedMember.getNickname())
                .createdAt(savedMember.getCreatedAt())
                .message("회원 가입이 완료되었습니다.")
                .build();
    }

    /**
     * 사용자명으로 회원 조회
     */
    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    /**
     * 이메일로 회원 조회
     */
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
    }

    /**
     * 회원 ID로 회원 조회
     */
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    /**
     * 사용자명 중복 체크
     */
    public boolean isUsernameExists(String username) {
        return memberRepository.existsByUsername(username);
    }

    /**
     * 이메일 중복 체크
     */
    public boolean isEmailExists(String email) {
        return memberRepository.existsByEmail(email);
    }

    /**
     * 로그인
     */
    public LoginResponse login(LoginRequest request) {
        log.info("로그인 요청: username={}", request.getUsername());

        // 회원 조회
        Member member = findByUsername(request.getUsername());

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 활성화 상태 확인
        if (!member.isActive()) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }

        // JWT 토큰 생성
        String accessToken = jwtService.generateAccessToken(member);
        String refreshToken = jwtService.generateRefreshToken(member);

        log.info("로그인 성공: memberId={}, username={}", member.getId(), member.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                .memberId(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .role(member.getRole().name())
                .message("로그인이 완료되었습니다.")
                .build();
    }

    /**
     * 토큰 갱신
     */
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        log.info("토큰 갱신 요청");

        // Refresh Token 검증
        if (!jwtService.validateToken(request.getRefreshToken())) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        // 토큰에서 사용자명 추출
        String username = jwtService.extractUsername(request.getRefreshToken());
        
        // 회원 조회
        Member member = findByUsername(username);

        // 활성화 상태 확인
        if (!member.isActive()) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }

        // 새로운 토큰 생성
        String newAccessToken = jwtService.generateAccessToken(member);
        String newRefreshToken = jwtService.generateRefreshToken(member);

        log.info("토큰 갱신 완료: memberId={}, username={}", member.getId(), member.getUsername());

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                .message("토큰이 갱신되었습니다.")
                .build();
    }

    /**
     * 프로필 조회
     */
    public ProfileResponse getProfile(String username) {
        log.info("프로필 조회 요청: username={}", username);

        Member member = findByUsername(username);

        return ProfileResponse.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .role(member.getRole().name())
                .active(member.isActive())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    /**
     * 프로필 수정
     */
    @Transactional
    public ProfileResponse updateProfile(String username, ProfileUpdateRequest request) {
        log.info("프로필 수정 요청: username={}", username);

        Member member = findByUsername(username);

        // 이메일 중복 체크 (다른 사용자가 사용 중인지 확인)
        if (request.getEmail() != null && !request.getEmail().equals(member.getEmail())) {
            if (memberRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }

        // 프로필 정보 업데이트
        member.updateProfile(
            request.getNickname() != null ? request.getNickname() : member.getNickname(),
            request.getEmail() != null ? request.getEmail() : member.getEmail(),
            request.getProfileImageUrl() != null ? request.getProfileImageUrl() : member.getProfileImageUrl()
        );

        log.info("프로필 수정 완료: memberId={}, username={}", member.getId(), member.getUsername());

        return getProfile(username);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(String username, PasswordChangeRequest request) {
        log.info("비밀번호 변경 요청: username={}", username);

        Member member = findByUsername(username);

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 및 저장
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        member.changePassword(encodedNewPassword);

        log.info("비밀번호 변경 완료: memberId={}, username={}", member.getId(), member.getUsername());
    }

    /**
     * 회원 목록 조회 (관리자용)
     */
    public MemberListResponse getMemberList(int page, int size) {
        log.info("회원 목록 조회 요청: page={}, size={}", page, size);

        // 페이징 처리
        int offset = page * size;
        
        // 전체 회원 수 조회
        long totalCount = memberRepository.count();
        
        // 회원 목록 조회 (간단한 구현, 실제로는 페이징 쿼리 사용)
        List<Member> members = memberRepository.findAll();
        
        // 페이징 적용
        List<Member> pagedMembers = members.stream()
                .skip(offset)
                .limit(size)
                .toList();

        // DTO 변환
        List<MemberListResponse.MemberSummary> memberSummaries = pagedMembers.stream()
                .map(member -> MemberListResponse.MemberSummary.builder()
                        .memberId(member.getId())
                        .username(member.getUsername())
                        .email(member.getEmail())
                        .nickname(member.getNickname())
                        .role(member.getRole().name())
                        .active(member.isActive())
                        .createdAt(member.getCreatedAt())
                        .updatedAt(member.getUpdatedAt())
                        .build())
                .toList();

        return MemberListResponse.builder()
                .members(memberSummaries)
                .totalCount(totalCount)
                .pageNumber(page)
                .pageSize(size)
                .build();
    }

    /**
     * 회원 권한 변경 (관리자용)
     */
    @Transactional
    public void updateMemberRole(Long memberId, MemberRoleUpdateRequest request) {
        log.info("회원 권한 변경 요청: memberId={}, newRole={}", memberId, request.getRole());

        Member member = findById(memberId);
        member.updateRole(request.getRole());

        log.info("회원 권한 변경 완료: memberId={}, username={}, newRole={}", 
                member.getId(), member.getUsername(), member.getRole());
    }

    /**
     * 회원 활성화/비활성화 (관리자용)
     */
    @Transactional
    public void toggleMemberStatus(Long memberId) {
        log.info("회원 상태 변경 요청: memberId={}", memberId);

        Member member = findById(memberId);
        
        if (member.isActive()) {
            member.deactivate();
            log.info("회원 비활성화 완료: memberId={}, username={}", member.getId(), member.getUsername());
        } else {
            member.activate();
            log.info("회원 활성화 완료: memberId={}, username={}", member.getId(), member.getUsername());
        }
    }

    /**
     * 회원 삭제 (관리자용)
     */
    @Transactional
    public void deleteMember(Long memberId) {
        log.info("회원 삭제 요청: memberId={}", memberId);

        Member member = findById(memberId);
        memberRepository.delete(member);

        log.info("회원 삭제 완료: memberId={}, username={}", memberId, member.getUsername());
    }
}
