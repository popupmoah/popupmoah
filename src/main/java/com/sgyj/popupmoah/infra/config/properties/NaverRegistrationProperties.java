package com.sgyj.popupmoah.infra.config.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@NoArgsConstructor
@ConfigurationProperties("spring.security.oauth2.client.registration")
public class NaverRegistrationProperties {

    private String provider;
    private String clientId;
    private String clientSecret;
    private String scope;
    private String clientAuthenticationMethod;
    private String authorizationGrantType;
    private String redirectUri;
    private String tokenUri;

}
