package com.nyamnyam.coach.global.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.nyamnyam.coach.**.repository")
public class MyBatisConfig {
}
