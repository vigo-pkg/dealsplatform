package com.dealsplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DealsPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(DealsPlatformApplication.class, args);
    }
}
