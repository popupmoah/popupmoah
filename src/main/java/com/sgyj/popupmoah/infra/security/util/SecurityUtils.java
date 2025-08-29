package com.sgyj.popupmoah.infra.security.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Slf4j
public class SecurityUtils {
    
    /**
     * 현재 인증된 사용자의 이메일을 반환합니다.
     */
    public static Optional<String> getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            return Optional.of((String) principal);
        }
        
        return Optional.empty();
    }
    
    /**
     * 현재 인증된 사용자의 권한을 반환합니다.
     */
    public static Collection<? extends GrantedAuthority> getCurrentUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptyList();
        }
        
        return authentication.getAuthorities();
    }
    
    /**
     * 현재 사용자가 특정 권한을 가지고 있는지 확인합니다.
     */
    public static boolean hasAuthority(String authority) {
        return getCurrentUserAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }
    
    /**
     * 현재 사용자가 관리자 권한을 가지고 있는지 확인합니다.
     */
    public static boolean isAdmin() {
        return hasAuthority("ROLE_ADMIN");
    }
    
    /**
     * 현재 사용자가 일반 사용자 권한을 가지고 있는지 확인합니다.
     */
    public static boolean isUser() {
        return hasAuthority("ROLE_USER");
    }
    
    /**
     * 현재 인증된 사용자인지 확인합니다.
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }
    
    /**
     * 현재 사용자의 인증 정보를 로그로 출력합니다.
     */
    public static void logCurrentUser() {
        if (isAuthenticated()) {
            String email = getCurrentUserEmail().orElse("Unknown");
            Collection<? extends GrantedAuthority> authorities = getCurrentUserAuthorities();
            log.debug("현재 사용자: {}, 권한: {}", email, authorities);
        } else {
            log.debug("인증되지 않은 사용자");
        }
    }
}
