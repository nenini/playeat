package com.nyamnyam.coach.ai.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AiErrorCode;
import org.springframework.stereotype.Component;

@Component
public class AiJsonResponseParser {

    private final ObjectMapper objectMapper;

    public AiJsonResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public DailyReportContent parseDailyReport(String rawText) {
        return parse(rawText, DailyReportContent.class);
    }

    public CoachFeedbackContent parseCoachFeedback(String rawText) {
        return parse(rawText, CoachFeedbackContent.class);
    }

    public AiQuestContent parseQuest(String rawText) {
        return parse(rawText, AiQuestContent.class);
    }

    private <T> T parse(String rawText, Class<T> type) {
        try {
            return objectMapper.readValue(extractJsonObject(rawText), type);
        } catch (JsonProcessingException exception) {
            throw new BusinessException(AiErrorCode.AI_RESPONSE_PARSE_FAILED);
        }
    }

    private String extractJsonObject(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            throw new BusinessException(AiErrorCode.AI_RESPONSE_PARSE_FAILED);
        }
        int start = rawText.indexOf('{');
        int end = rawText.lastIndexOf('}');
        if (start < 0 || end < start) {
            throw new BusinessException(AiErrorCode.AI_RESPONSE_PARSE_FAILED);
        }
        return rawText.substring(start, end + 1);
    }
}
