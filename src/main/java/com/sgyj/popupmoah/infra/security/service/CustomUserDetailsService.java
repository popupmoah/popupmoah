package com.sgyj.popupmoah.infra.security.service;

import com.sgyj.popupmoah.community.domain.entity.Member;
import com.sgyj.popupmoah.community.domain.entity.MemberRole;
import com.sgyj.popupmoah.community.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {
    
    private final MemberRepository memberRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("사용자 인증 시도: {}", email);
        
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
        
        return createUserDetails(member);
    }
    
    /**
     * Member 엔티티를 UserDetails로 변환합니다.
     */
    private UserDetails createUserDetails(Member member) {
        Collection<? extends GrantedAuthority> authorities = getAuthorities(member.getRole());
        
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
    
    /**
     * MemberRole을 GrantedAuthority로 변환합니다.
     */
    private Collection<? extends GrantedAuthority> getAuthorities(MemberRole role) {
        if (role == null) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
