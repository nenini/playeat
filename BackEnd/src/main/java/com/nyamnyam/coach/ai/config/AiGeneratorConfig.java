package com.nyamnyam.coach.ai.config;

import com.nyamnyam.coach.ai.service.AiTextGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GmsProperties.class)
public class AiGeneratorConfig {

    private static final Logger log = LoggerFactory.getLogger(AiGeneratorConfig.class);

    @Bean
    ApplicationRunner aiGeneratorStartupLogger(
            GmsProperties properties,
            ObjectProvider<AiTextGenerator> aiTextGeneratorProvider
    ) {
        return args -> {
            AiTextGenerator generator = aiTextGeneratorProvider.getIfAvailable();
            String generatorName = generator == null ? "none" : generator.getClass().getSimpleName();
            log.info(
                    "AI generator configured provider={} gmsEnabled={} model={} baseUrl={} timeoutMs={} apiKeyPresent={}",
                    generatorName,
                    properties.isEnabled(),
                    properties.getModel(),
                    properties.getBaseUrl(),
                    properties.getTimeoutMs(),
                    properties.getApiKey() != null && !properties.getApiKey().isBlank()
            );
        };
    }
}
