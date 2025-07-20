package com.sgyj.popupmoah.config;

import com.sgyj.popupmoah.module.community.entity.Member;
import com.sgyj.popupmoah.module.community.repository.MemberRepository;
import com.sgyj.popupmoah.module.community.security.JwtAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import org.springframework.cache.annotation.Cacheable;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    // Removed userCache field, will use @Cacheable instead

    public JwtAuthenticationFilter(JwtUtil jwtUtil, MemberRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

    @Cacheable(value = "userCache", key = "#username")
    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElse(null);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                username = jwtUtil.getUsername(token);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Member member = getMemberByUsername(username);
            if (member != null) {
                JwtAuthentication jwtAuth = new JwtAuthentication(member.getId(), member.getUsername());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        jwtAuth, null, java.util.Collections.emptyList());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
} 