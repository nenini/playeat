package com.nyamnyam.coach.ai.service;

import com.nyamnyam.coach.ai.service.prompt.AiQuestPrompt;
import com.nyamnyam.coach.ai.service.prompt.CoachFeedbackPrompt;
import com.nyamnyam.coach.ai.service.prompt.DailyReportPrompt;
import com.nyamnyam.coach.ai.service.prompt.WeeklyReportPrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "gms", name = "enabled", havingValue = "false", matchIfMissing = true)
public class PlaceholderAiTextGenerator implements AiTextGenerator {

    private static final Logger log = LoggerFactory.getLogger(PlaceholderAiTextGenerator.class);

    @Override
    public String generateCoachFeedback(CoachFeedbackPrompt prompt) {
        log.info("AI generator selected provider=placeholder feature=coach-feedback model=placeholder");
        return """
                {
                  "message": "이번 식사는 기록을 바탕으로 확인했어요. 다음 끼니에는 단백질이나 채소 반찬을 한 가지 더해보세요."
                }
                """;
    }

    @Override
    public String generateDailyReport(DailyReportPrompt prompt) {
        log.info("AI generator selected provider=placeholder feature=daily-report model=placeholder");
        return """
                {
                  "summary": "기록된 영양 데이터를 바탕으로 오늘 식단을 확인했어요.",
                  "strengths": ["오늘 식단을 기록해서 영양 상태를 점검할 수 있어요."],
                  "warnings": ["목표보다 부족하거나 과한 영양소를 확인해보세요."],
                  "nextAction": "다음 끼니에는 단백질이나 채소 반찬을 한 가지 추가해보세요."
                }
                """;
    }

    @Override
    public String generateWeeklyReport(WeeklyReportPrompt prompt) {
        log.info("AI generator selected provider=placeholder feature=weekly-report model=placeholder");
        return "주간 리포트 생성은 현재 일일 AI 작업 범위에 포함되지 않습니다.";
    }

    @Override
    public String generateDailyQuest(AiQuestPrompt prompt) {
        log.info("AI generator selected provider=placeholder feature=daily-quest model=placeholder");
        Long selectedTemplateId = prompt.availableQuestTemplates().isEmpty()
                ? null
                : prompt.availableQuestTemplates().get(0).templateId();
        return """
                {
                  "selectedTemplateId": %s,
                  "customTitle": "오늘 식단 기록 퀘스트",
                  "customDescription": "오늘의 식단 기록을 완료해서 보스에게 피해를 주세요."
                }
                """.formatted(selectedTemplateId == null ? "null" : selectedTemplateId.toString());
    }

    @Override
    public String modelName() {
        return "placeholder";
    }
}
