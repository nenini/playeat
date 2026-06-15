package com.nyamnyam.coach.ai.service.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AiErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiJsonResponseParserTest {

    private final AiJsonResponseParser parser = new AiJsonResponseParser(new ObjectMapper());

    @Test
    void parseDailyReport() {
        DailyReportContent content = parser.parseDailyReport("""
                {
                  "summary": "good day",
                  "strengths": ["recorded meals"],
                  "warnings": ["watch sodium"],
                  "nextAction": "add protein"
                }
                """);

        assertThat(content.summary()).isEqualTo("good day");
        assertThat(content.strengths()).containsExactly("recorded meals");
        assertThat(content.warnings()).containsExactly("watch sodium");
        assertThat(content.nextAction()).isEqualTo("add protein");
    }

    @Test
    void parseQuestFromMarkdownWrappedJson() {
        AiQuestContent content = parser.parseQuest("""
                ```json
                {
                  "selectedTemplateId": 3,
                  "customTitle": "sugar guard",
                  "customDescription": "keep sugar low"
                }
                ```
                """);

        assertThat(content.selectedTemplateId()).isEqualTo(3L);
        assertThat(content.customTitle()).isEqualTo("sugar guard");
        assertThat(content.customDescription()).isEqualTo("keep sugar low");
    }

    @Test
    void parseInvalidJsonThrowsBusinessException() {
        assertThatThrownBy(() -> parser.parseCoachFeedback("not-json"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AiErrorCode.AI_RESPONSE_PARSE_FAILED);
    }
}
