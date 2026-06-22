package com.nyamnyam.coach.ai.service;

import com.nyamnyam.coach.ai.service.prompt.AiQuestPrompt;
import com.nyamnyam.coach.ai.service.prompt.CharacterMoodPrompt;
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
        String firstPattern = prompt.repeatedPatterns().isEmpty()
                ? "이번 주 식단 기록을 바탕으로 다음 주 목표를 정해보세요."
                : prompt.repeatedPatterns().get(0);
        return """
                {
                  "summary": "이번 주 식단 기록과 영양 분석을 바탕으로 주간 리포트를 생성했어요. 평균 건강 점수는 %d점입니다.",
                  "strengths": ["주간 식단 흐름을 기록해서 식습관을 점검할 수 있어요.", "반복되는 영양 패턴을 확인할 수 있어요."],
                  "warnings": ["%s", "공식 건강 자료 기반 조언은 개인 질환의 진단이나 치료 지시가 아니에요."],
                  "nextAction": "다음 주에는 가장 자주 반복된 영양 이슈 한 가지를 정해서 식사 선택을 조정해보세요."
                }
                """.formatted(prompt.averageHealthScore(), jsonEscape(firstPattern));
    }

    private String jsonEscape(String value) {
        if (value == null) return "";
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
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
    public String selectCharacterMood(CharacterMoodPrompt prompt) {
        log.info("AI generator selected provider=placeholder feature=character-mood model=placeholder");
        int healthScore = prompt.healthScore() == null ? 0 : prompt.healthScore();
        String mood;
        if (healthScore >= 80) {
            mood = "MUSCLE";
        } else if (healthScore >= 60) {
            mood = "NORMAL";
        } else if (healthScore >= 40) {
            mood = "HUNGRY";
        } else {
            mood = "CHUBBY";
        }
        return """
                {
                  "mood": "%s",
                  "reason": "일간 리포트 건강 점수를 기준으로 선택했습니다."
                }
                """.formatted(mood);
    }

    @Override
    public String modelName() {
        return "placeholder";
    }
}
