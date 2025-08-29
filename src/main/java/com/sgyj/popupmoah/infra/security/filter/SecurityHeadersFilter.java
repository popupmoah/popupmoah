package com.sgyj.popupmoah.infra.security.filter;

import com.sgyj.popupmoah.infra.config.SecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityHeadersFilter extends OncePerRequestFilter {
    
    private final SecurityProperties securityProperties;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // XSS 보호
        response.setHeader("X-Content-Type-Options", securityProperties.getXContentTypeOptions());
        response.setHeader("X-XSS-Protection", securityProperties.getXXssProtection());
        response.setHeader("X-Frame-Options", securityProperties.getXFrameOptions());
        
        // Content Security Policy
        response.setHeader("Content-Security-Policy", securityProperties.getContentSecurityPolicy());
        
        // Referrer Policy
        response.setHeader("Referrer-Policy", securityProperties.getReferrerPolicy());
        
        // Permissions Policy (구 Feature Policy)
        response.setHeader("Permissions-Policy", securityProperties.getPermissionsPolicy());
        
        // Strict Transport Security (HTTPS에서만 적용)
        if (request.isSecure()) {
            response.setHeader("Strict-Transport-Security", 
                    "max-age=31536000; includeSubDomains; preload");
        }
        
        // Cache Control
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        // Server 정보 숨기기
        response.setHeader("Server", "");
        
        // X-Powered-By 헤더 제거
        response.setHeader("X-Powered-By", "");
        
        log.debug("보안 헤더 추가 완료: {}", request.getRequestURI());
        
        filterChain.doFilter(request, response);
    }
}
