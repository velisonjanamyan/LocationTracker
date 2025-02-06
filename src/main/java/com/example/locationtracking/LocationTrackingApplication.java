package com.example.locationtracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LocationTrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocationTrackingApplication.class, args);
    }
}
