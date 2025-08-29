package com.sgyj.popupmoah.infra.config;

import com.sgyj.popupmoah.infra.security.JwtAccessDeniedHandler;
import com.sgyj.popupmoah.infra.security.JwtAuthenticationEntryPoint;
import com.sgyj.popupmoah.infra.security.JwtAuthenticationFilter;
import com.sgyj.popupmoah.infra.security.filter.SecurityHeadersFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final SecurityHeadersFilter securityHeadersFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (JWT 사용 시 불필요)
            .csrf(AbstractHttpConfigurer::disable)
            
            // CORS 설정
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 세션 관리 정책 설정 (STATELESS)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 요청별 권한 설정
            .authorizeHttpRequests(authz -> authz
                // 공개 엔드포인트
                .requestMatchers(
                    "/api/auth/**",
                    "/api/popupstores/search",
                    "/api/popupstores/active",
                    "/api/popupstores/{id}",
                    "/api/popupstores/{id}/view",
                    "/api/popupstores/{id}/like",
                    "/api/popupstores/{id}/unlike",
                    "/api/community/reviews/**",
                    "/api/community/posts/**",
                    "/actuator/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/h2-console/**"
                ).permitAll()
                
                // 관리자 전용 엔드포인트
                .requestMatchers(
                    "/api/admin/**"
                ).hasRole("ADMIN")
                
                // 인증된 사용자만 접근 가능한 엔드포인트
                .requestMatchers(
                    "/api/reservations/**",
                    "/api/members/profile",
                    "/api/members/update"
                ).authenticated()
                
                // 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // 예외 처리 설정
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )
            
            // 보안 헤더 필터 추가 (가장 먼저 실행)
            .addFilterBefore(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class)
            // JWT 필터 추가
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 오리진 설정
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 인증 정보 포함 허용
        configuration.setAllowCredentials(true);
        
        // 노출할 헤더 설정
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}