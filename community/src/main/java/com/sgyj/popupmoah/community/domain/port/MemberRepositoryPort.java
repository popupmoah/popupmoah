package com.sgyj.popupmoah.community.domain.port;

import com.sgyj.popupmoah.community.domain.entity.Member;

import java.util.List;
import java.util.Optional;

/**
 * 멤버 리포지토리 포트
 * 도메인이 정의하는 인터페이스로, 인프라스트럭처가 구현
 */
public interface MemberRepositoryPort {
    
    /**
     * 멤버를 저장합니다.
     */
    Member save(Member member);
    
    /**
     * ID로 멤버를 조회합니다.
     */
    Optional<Member> findById(Long id);
    
    /**
     * 모든 멤버를 조회합니다.
     */
    List<Member> findAll();
    
    /**
     * 사용자명으로 멤버를 조회합니다.
     */
    Optional<Member> findByUsername(String username);
    
    /**
     * 이메일로 멤버를 조회합니다.
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * 멤버를 삭제합니다.
     */
    void deleteById(Long id);
    
    /**
     * 멤버가 존재하는지 확인합니다.
     */
    boolean existsById(Long id);
    
    /**
     * 사용자명이 존재하는지 확인합니다.
     */
    boolean existsByUsername(String username);
    
    /**
     * 이메일이 존재하는지 확인합니다.
     */
    boolean existsByEmail(String email);
} 