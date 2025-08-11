package com.sgyj.popupmoah.community.domain.aggregate;

import com.sgyj.popupmoah.community.domain.entity.Member;
import com.sgyj.popupmoah.community.domain.port.MemberRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * 멤버 어그리게이트 루트
 * 멤버 도메인에 대한 접근을 제어하는 루트 엔티티
 */
public class MemberAggregate {
    
    private final MemberRepositoryPort repository;
    
    public MemberAggregate(MemberRepositoryPort repository) {
        this.repository = repository;
    }
    
    /**
     * 멤버를 생성합니다.
     */
    public Member create(Member member) {
        // 도메인 규칙 검증
        validateMember(member);
        return repository.save(member);
    }
    
    /**
     * 멤버를 조회합니다.
     */
    public Optional<Member> findById(Long id) {
        return repository.findById(id);
    }
    
    /**
     * 모든 멤버를 조회합니다.
     */
    public List<Member> findAll() {
        return repository.findAll();
    }
    
    /**
     * 사용자명으로 멤버를 조회합니다.
     */
    public Optional<Member> findByUsername(String username) {
        return repository.findByUsername(username);
    }
    
    /**
     * 이메일로 멤버를 조회합니다.
     */
    public Optional<Member> findByEmail(String email) {
        return repository.findByEmail(email);
    }
    
    /**
     * 멤버를 업데이트합니다.
     */
    public Member update(Long id, Member member) {
        Optional<Member> existing = repository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("멤버를 찾을 수 없습니다: " + id);
        }
        
        // 도메인 규칙 검증
        validateMember(member);
        
        Member updated = existing.get();
        // 불변성을 유지하면서 업데이트
        updated = Member.builder()
                .id(updated.getId())
                .username(member.getUsername())
                .password(member.getPassword())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
        
        return repository.save(updated);
    }
    
    /**
     * 멤버를 삭제합니다.
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("멤버를 찾을 수 없습니다: " + id);
        }
        repository.deleteById(id);
    }
    
    /**
     * 멤버 도메인 규칙을 검증합니다.
     */
    private void validateMember(Member member) {
        if (member.getUsername() == null || member.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명은 필수입니다.");
        }
        
        if (member.getPassword() == null || member.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        
        if (member.getEmail() != null && !member.getEmail().contains("@")) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
    }
} 