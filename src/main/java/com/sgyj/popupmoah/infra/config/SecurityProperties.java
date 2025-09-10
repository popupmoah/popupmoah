package com.sgyj.popupmoah.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "security.headers")
public class SecurityProperties {
    
    private String contentSecurityPolicy;
    private String xFrameOptions;
    private String xContentTypeOptions;
    private String xXssProtection;
    private String referrerPolicy;
    private String permissionsPolicy;
}
