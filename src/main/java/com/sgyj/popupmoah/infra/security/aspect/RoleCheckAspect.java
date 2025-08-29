package com.sgyj.popupmoah.infra.security.aspect;

import com.sgyj.popupmoah.infra.security.annotation.RequireRole;
import com.sgyj.popupmoah.infra.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class RoleCheckAspect {
    
    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        log.debug("권한 검증 시작: {}", joinPoint.getSignature().getName());
        
        if (!SecurityUtils.isAuthenticated()) {
            throw new AccessDeniedException("인증이 필요합니다.");
        }
        
        String[] requiredRoles = requireRole.value();
        if (requiredRoles.length == 0) {
            // 역할이 지정되지 않은 경우 통과
            return joinPoint.proceed();
        }
        
        Collection<? extends GrantedAuthority> userAuthorities = SecurityUtils.getCurrentUserAuthorities();
        List<String> userRoles = userAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        boolean hasRequiredRole;
        if (requireRole.requireAll()) {
            // 모든 역할이 필요한 경우 (AND 조건)
            hasRequiredRole = Arrays.stream(requiredRoles)
                    .allMatch(role -> userRoles.contains("ROLE_" + role));
        } else {
            // 하나의 역할만 있으면 되는 경우 (OR 조건)
            hasRequiredRole = Arrays.stream(requiredRoles)
                    .anyMatch(role -> userRoles.contains("ROLE_" + role));
        }
        
        if (!hasRequiredRole) {
            String currentUser = SecurityUtils.getCurrentUserEmail().orElse("Unknown");
            log.warn("권한 부족: 사용자={}, 필요 권한={}, 현재 권한={}", 
                    currentUser, Arrays.toString(requiredRoles), userRoles);
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
        
        log.debug("권한 검증 통과: {}", joinPoint.getSignature().getName());
        return joinPoint.proceed();
    }
}
