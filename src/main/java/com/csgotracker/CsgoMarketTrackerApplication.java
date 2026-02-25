package com.csgotracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CsgoMarketTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsgoMarketTrackerApplication.class, args);
    }
}