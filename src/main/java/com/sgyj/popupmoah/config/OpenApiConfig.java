package com.sgyj.popupmoah.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * Swagger(OpenAPI) 문서 기본 정보 설정
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("PopupMoah API")
                .description("팝업모아 REST API 문서입니다.")
                .version("v1.0.0"));
    }
} 