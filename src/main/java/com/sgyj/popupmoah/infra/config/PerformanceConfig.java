package com.sgyj.popupmoah.infra.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 성능 모니터링 설정
 * Micrometer를 사용한 성능 메트릭 수집
 */
@Configuration
@EnableAspectJAutoProxy
public class PerformanceConfig {

    /**
     * @Timed 어노테이션을 사용한 메서드 실행 시간 측정
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
