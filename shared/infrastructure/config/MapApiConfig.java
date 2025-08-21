package com.sgyj.popupmoah.shared.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 지도 API 설정 Properties
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "map")
public class MapApiConfig {

    private Kakao kakao = new Kakao();
    private Naver naver = new Naver();

    @Getter
    @Setter
    public static class Kakao {
        private String apiKey;
        private String geocodingUrl;
        private String placesUrl;
    }

    @Getter
    @Setter
    public static class Naver {
        private String clientId;
        private String clientSecret;
        private String geocodingUrl;
        private String placesUrl;
    }
}

