package com.sgyj.popupmoah.api.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 모니터링 설정 클래스
 * Micrometer, Prometheus, Spring Boot Actuator 설정
 */
@Configuration
public class MonitoringConfig {

    /**
     * Prometheus 메트릭 레지스트리 설정
     */
    @Bean
    @Profile("prod")
    public PrometheusMeterRegistry prometheusMeterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    /**
     * @Timed 어노테이션을 위한 AOP 설정
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * 메트릭 커스터마이저 설정
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags("application", "popupmoah")
                .commonTags("environment", getEnvironment())
                .commonTags("version", getApplicationVersion())
                .meterFilter(MeterFilter.denyNameStartsWith("jvm.threads"))
                .meterFilter(MeterFilter.denyNameStartsWith("jvm.gc"))
                .meterFilter(MeterFilter.denyNameStartsWith("jvm.memory"));
    }

    /**
     * 환경 정보 반환
     */
    private String getEnvironment() {
        String env = System.getProperty("spring.profiles.active");
        return env != null ? env : "default";
    }

    /**
     * 애플리케이션 버전 정보 반환
     */
    private String getApplicationVersion() {
        String version = this.getClass().getPackage().getImplementationVersion();
        return version != null ? version : "unknown";
    }
}
