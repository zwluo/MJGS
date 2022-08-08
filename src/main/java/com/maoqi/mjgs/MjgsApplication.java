package com.maoqi.mjgs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
//@EnableCaching
public class MjgsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MjgsApplication.class, args);
    }

}
