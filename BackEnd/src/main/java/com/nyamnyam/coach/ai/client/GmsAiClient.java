package com.nyamnyam.coach.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.ai.config.GmsProperties;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AiErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "gms", name = "enabled", havingValue = "true")
public class GmsAiClient {

    private static final Logger log = LoggerFactory.getLogger(GmsAiClient.class);
    private static final String CHAT_COMPLETIONS_ENDPOINT = "/chat/completions";

    private final GmsProperties properties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public GmsAiClient(GmsProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(properties.getTimeoutMs()));
        requestFactory.setReadTimeout(Duration.ofMillis(properties.getTimeoutMs()));
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    public String createResponse(String prompt) {
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            log.warn("GMS AI request blocked reason=missing-api-key model={}", properties.getModel());
            throw new BusinessException(AiErrorCode.AI_PROVIDER_UNAVAILABLE);
        }

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("model", properties.getModel());
        request.put("messages", messages(prompt));

        long startedAt = System.currentTimeMillis();
        log.info(
                "GMS AI request start endpoint={} model={} promptLength={}",
                CHAT_COMPLETIONS_ENDPOINT,
                properties.getModel(),
                prompt == null ? 0 : prompt.length()
        );
        try {
            String rawResponse = restClient.post()
                    .uri(CHAT_COMPLETIONS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .body(request)
                    .retrieve()
                    .body(String.class);
            String text = extractText(rawResponse);
            log.info(
                    "GMS AI request success endpoint={} model={} elapsedMs={} responseLength={}",
                    CHAT_COMPLETIONS_ENDPOINT,
                    properties.getModel(),
                    System.currentTimeMillis() - startedAt,
                    text == null ? 0 : text.length()
            );
            return text;
        } catch (RestClientResponseException exception) {
            log.warn(
                    "GMS AI request failed endpoint={} model={} status={} elapsedMs={}",
                    CHAT_COMPLETIONS_ENDPOINT,
                    properties.getModel(),
                    exception.getStatusCode(),
                    System.currentTimeMillis() - startedAt
            );
            throw new BusinessException(AiErrorCode.AI_PROVIDER_UNAVAILABLE);
        } catch (RestClientException exception) {
            log.warn(
                    "GMS AI request failed endpoint={} model={} error={} elapsedMs={}",
                    CHAT_COMPLETIONS_ENDPOINT,
                    properties.getModel(),
                    exception.getClass().getSimpleName(),
                    System.currentTimeMillis() - startedAt
            );
            throw new BusinessException(AiErrorCode.AI_PROVIDER_UNAVAILABLE);
        }
    }

    private List<Map<String, String>> messages(String prompt) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(message("developer", "사용자 지시를 정확히 따르고 요청한 내용만 한국어로 반환하세요."));
        messages.add(message("user", prompt));
        return messages;
    }

    private Map<String, String> message(String role, String content) {
        Map<String, String> message = new LinkedHashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private String extractText(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && !choices.isEmpty()) {
                JsonNode content = choices.get(0).path("message").get("content");
                if (content != null && content.isTextual()) {
                    return content.asText();
                }
            }

            JsonNode outputText = root.get("output_text");
            if (outputText != null && outputText.isTextual()) {
                return outputText.asText();
            }

            JsonNode output = root.get("output");
            if (output != null && output.isArray()) {
                for (JsonNode outputItem : output) {
                    JsonNode content = outputItem.get("content");
                    if (content == null || !content.isArray()) {
                        continue;
                    }
                    for (JsonNode contentItem : content) {
                        JsonNode text = contentItem.get("text");
                        if (text != null && text.isTextual()) {
                            return text.asText();
                        }
                    }
                }
            }
            throw new BusinessException(AiErrorCode.AI_RESPONSE_FAILED);
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(AiErrorCode.AI_RESPONSE_FAILED);
        }
    }
}
