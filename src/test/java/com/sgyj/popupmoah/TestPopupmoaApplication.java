package com.sgyj.popupmoah;

import org.springframework.boot.SpringApplication;

public class TestPopupmoaApplication {

    public static void main(String[] args) {
        SpringApplication.from(PopupmoahApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
