package com.nyamnyam.coach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NyamNyamCoachApplication {

    public static void main(String[] args) {
        SpringApplication.run(NyamNyamCoachApplication.class, args);
    }
}
