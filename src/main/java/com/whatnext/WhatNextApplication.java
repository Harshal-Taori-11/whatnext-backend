package com.whatnext;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WhatNextApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhatNextApplication.class, args);
    }
}
