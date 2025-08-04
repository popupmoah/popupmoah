package com.sgyj.popupmoah.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authorization.AuthorizationDecision;

@Configuration("jwtSecurityConfig")
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/h2-console/**", "/actuator/**")
                    .access((authentication, context) -> {
                        String profilesParam = context.getRequest().getServletContext()
                            .getInitParameter("spring.profiles.active");
                        String[] activeProfiles = profilesParam != null ? profilesParam.split(",") : new String[0];
                        for (String profile : activeProfiles) {
                            if (profile.trim().equals("dev") || profile.trim().equals("test")) {
                                return new AuthorizationDecision(true); // 허용
                            }
                        }
                        return new AuthorizationDecision(false); // 거부
                    });
                auth.anyRequest().authenticated();
            })
            .headers(headers -> headers.frameOptions(c -> c.disable()))
            .formLogin(form -> form.permitAll())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
} 