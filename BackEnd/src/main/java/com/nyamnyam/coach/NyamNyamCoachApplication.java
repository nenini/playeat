package com.nyamnyam.coach;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nyamnyam.coach.**.repository")
public class NyamNyamCoachApplication {

    public static void main(String[] args) {
        SpringApplication.run(NyamNyamCoachApplication.class, args);
    }
}
