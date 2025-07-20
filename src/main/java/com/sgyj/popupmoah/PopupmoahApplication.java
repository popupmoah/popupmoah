package com.sgyj.popupmoah;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching; // 캐시 활성화
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync; // 비동기 활성화

@EnableJpaAuditing
@EnableCaching // 스프링 캐시 활성화
@EnableAsync // 스프링 비동기 활성화
@SpringBootApplication
@EnableEncryptableProperties
public class PopupmoahApplication {

    public static void main(String[] args) {
        SpringApplication.run(PopupmoahApplication.class, args);
    }

}
