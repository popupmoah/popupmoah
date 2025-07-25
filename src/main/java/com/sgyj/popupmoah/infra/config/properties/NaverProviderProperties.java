package com.sgyj.popupmoah.infra.config.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("spring.security.oauth2.client.provider.naver")
public class NaverProviderProperties {

    private String issuerUri;
    private String tokenUri;
}
