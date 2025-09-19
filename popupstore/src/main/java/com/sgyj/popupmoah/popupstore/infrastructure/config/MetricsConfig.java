package com.sgyj.popupmoah.popupstore.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 팝업스토어 모듈용 Prometheus 메트릭 설정 클래스
 * 팝업스토어 관련 커스텀 메트릭을 정의합니다.
 */
@Configuration
public class MetricsConfig {

    /**
     * 팝업스토어 조회 카운터
     */
    @Bean
    public Counter popupStoreViewCounter(MeterRegistry meterRegistry) {
        return Counter.builder("popupstore.views.total")
                .description("Total number of popup store views")
                .register(meterRegistry);
    }

    /**
     * 팝업스토어 생성 카운터
     */
    @Bean
    public Counter popupStoreCreateCounter(MeterRegistry meterRegistry) {
        return Counter.builder("popupstore.creates.total")
                .description("Total number of popup store creations")
                .register(meterRegistry);
    }

    /**
     * API 응답 시간 타이머
     */
    @Bean
    public Timer apiResponseTimer(MeterRegistry meterRegistry) {
        return Timer.builder("api.response.time")
                .description("API response time")
                .register(meterRegistry);
    }
}
