package com.sgyj.popupmoa;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class PopupmoaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PopupmoaApplication.class, args);
    }

}
