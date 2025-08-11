package com.sgyj.popupmoah;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.sgyj.popupmoah",
    "com.sgyj.popupmoah.adapter",
    "com.sgyj.popupmoah.application",
    "com.sgyj.popupmoah.domain",
    "com.sgyj.popupmoah.infrastructure"
})
@EntityScan(basePackages = {
    "com.sgyj.popupmoah.domain",
    "com.sgyj.popupmoah.shared"
})
@EnableJpaRepositories(basePackages = {
    "com.sgyj.popupmoah.adapter.persistence",
    "com.sgyj.popupmoah.infrastructure"
})
public class PopupmoahApplication {

    public static void main(String[] args) {
        SpringApplication.run(PopupmoahApplication.class, args);
    }
}
