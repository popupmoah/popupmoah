package com.sgyj.popupmoa;

import org.springframework.boot.SpringApplication;

public class TestPopupmoaApplication {

    public static void main(String[] args) {
        SpringApplication.from(PopupmoaApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
