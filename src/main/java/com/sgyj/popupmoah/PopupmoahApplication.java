package com.sgyj.popupmoah;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableEncryptableProperties
public class PopupmoahApplication {

    public static void main(String[] args) {
        SpringApplication.run(PopupmoahApplication.class, args);
    }

}
