package com.nyamnyam.coach.ai.rag.config;

import com.nyamnyam.coach.ai.rag.service.HealthGuideIngestionService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HealthGuideRagProperties.class)
public class HealthGuideRagConfig {

    @Bean
    ApplicationRunner healthGuideIngestionRunner(
            HealthGuideRagProperties properties,
            HealthGuideIngestionService ingestionService
    ) {
        return args -> {
            if (properties.isIngestionEnabled()) {
                ingestionService.ingestSeedDocuments();
            }
        };
    }
}
