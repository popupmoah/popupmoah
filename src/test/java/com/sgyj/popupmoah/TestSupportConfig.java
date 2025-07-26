package com.sgyj.popupmoah;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import com.sgyj.popupmoah.config.JwtUtil;

@TestConfiguration
@Profile("test")
public class TestSupportConfig {
    @Bean(name = "testWebSecurityCustomizer")
    @Profile("test")
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/**");
    }

    @Bean(name = "testJwtUtil")
    @Primary
    @Profile("test")
    public JwtUtil jwtUtil() {
        return new JwtUtil("testtesttesttesttesttesttesttest12");
    }
} 