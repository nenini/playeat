package com.nyamnyam.coach.ai.service;

import com.nyamnyam.coach.ai.client.GmsAiClient;
import com.nyamnyam.coach.ai.config.GmsProperties;
import com.nyamnyam.coach.ai.service.prompt.AiQuestPrompt;
import com.nyamnyam.coach.ai.service.prompt.CoachFeedbackPrompt;
import com.nyamnyam.coach.ai.service.prompt.DailyReportPrompt;
import com.nyamnyam.coach.ai.service.prompt.QuestTemplatePrompt;
import com.nyamnyam.coach.ai.service.prompt.WeeklyReportPrompt;
import com.nyamnyam.coach.nutrition.dto.response.NutrientAnalysisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "gms", name = "enabled", havingValue = "true")
public class GmsAiTextGenerator implements AiTextGenerator {

    private static final Logger log = LoggerFactory.getLogger(GmsAiTextGenerator.class);

    private final GmsAiClient gmsAiClient;
    private final GmsProperties properties;

    public GmsAiTextGenerator(GmsAiClient gmsAiClient, GmsProperties properties) {
        this.gmsAiClient = gmsAiClient;
        this.properties = properties;
    }

    @Override
    public String generateCoachFeedback(CoachFeedbackPrompt prompt) {
        log.info("AI generator selected provider=gms feature=coach-feedback model={}", properties.getModel());
        return gmsAiClient.createResponse("""
                당신은 식습관 개선을 돕는 영양 코치입니다.
                반드시 JSON만 반환하세요. 마크다운은 사용하지 마세요.
                질병을 진단하지 말고, 지금 식사 기록을 바탕으로 짧고 실천 가능한 조언을 한국어로 작성하세요.

                출력 형식:
                {"message":"string"}

                코치 정보:
                이름: %s
                말투: %s

                식사 영양 정보:
                칼로리: %s
                단백질(g): %s
                탄수화물(g): %s
                지방(g): %s
                """.formatted(
                prompt.coachName(),
                prompt.coachTone(),
                prompt.calories(),
                prompt.proteinG(),
                prompt.carbsG(),
                prompt.fatG()
        ));
    }

    @Override
    public String generateDailyReport(DailyReportPrompt prompt) {
        log.info("AI generator selected provider=gms feature=daily-report model={}", properties.getModel());
        StringBuilder nutrients = new StringBuilder();
        for (NutrientAnalysisResponse nutrient : prompt.nutrients()) {
            nutrients.append("- ")
                    .append(nutrient.name())
                    .append(": 섭취 ")
                    .append(nutrient.current())
                    .append(nutrient.unit())
                    .append(", 목표 ")
                    .append(nutrient.target())
                    .append(nutrient.unit())
                    .append(", 달성률 ")
                    .append(nutrient.achievementRate())
                    .append("%, 상태 ")
                    .append(nutrient.status())
                    .append('\n');
        }

        String meals = prompt.mealSummaries().isEmpty()
                ? "기록된 식단 없음"
                : String.join("\n", prompt.mealSummaries());

        return gmsAiClient.createResponse("""
                당신은 식단 기록 기반 일간 영양 리포트를 작성하는 한국어 코치입니다.
                반드시 JSON만 반환하세요. 마크다운은 사용하지 마세요.
                음식명과 섭취량을 반영해서 구체적으로 말하되, 의학적 진단처럼 쓰지 마세요.
                사용자가 바로 읽을 수 있게 짧고 실행 가능한 문장으로 작성하세요.

                출력 규칙:
                - summary: 2문장 이내
                - strengths: 1~2개
                - warnings: 1~2개
                - nextAction: 1~2문장

                출력 형식:
                {
                  "summary": "string",
                  "strengths": ["string"],
                  "warnings": ["string"],
                  "nextAction": "string"
                }

                일간 분석 정보:
                날짜: %s
                건강 점수: %d

                기록된 식단:
                %s

                영양소 섭취 정보:
                %s
                """.formatted(prompt.date(), prompt.healthScore(), meals, nutrients));
    }

    @Override
    public String generateWeeklyReport(WeeklyReportPrompt prompt) {
        log.info("AI generator selected provider=gms feature=weekly-report model={}", properties.getModel());
        return gmsAiClient.createResponse("""
                반드시 JSON만 반환하세요.
                주간 리포트는 이후 구현 범위에서 처리합니다.
                시작일: %s
                종료일: %s
                """.formatted(prompt.startDate(), prompt.endDate()));
    }

    @Override
    public String generateDailyQuest(AiQuestPrompt prompt) {
        log.info("AI generator selected provider=gms feature=daily-quest model={}", properties.getModel());
        StringBuilder templates = new StringBuilder();
        for (QuestTemplatePrompt template : prompt.availableQuestTemplates()) {
            templates.append("- templateId: ").append(template.templateId())
                    .append(", title: ").append(template.title())
                    .append(", description: ").append(template.description())
                    .append(", questType: ").append(template.questType())
                    .append(", conditionCategory: ").append(template.conditionCategory())
                    .append(", metricType: ").append(template.metricType())
                    .append(", comparisonType: ").append(template.comparisonType())
                    .append(", aggregationType: ").append(template.aggregationType())
                    .append(", evaluationScope: ").append(template.evaluationScope())
                    .append(", thresholdValue: ").append(template.thresholdValue())
                    .append(", thresholdMinValue: ").append(template.thresholdMinValue())
                    .append(", thresholdMaxValue: ").append(template.thresholdMaxValue())
                    .append(", thresholdUnit: ").append(template.thresholdUnit())
                    .append(", targetValue: ").append(template.targetValue())
                    .append(", unit: ").append(template.unit())
                    .append('\n');
        }

        return gmsAiClient.createResponse("""
                당신은 보스전 참여자에게 줄 개인 퀘스트 템플릿을 하나 선택하는 보조자입니다.
                반드시 JSON만 반환하세요. 마크다운은 사용하지 마세요.
                availableQuestTemplates에 있는 templateId 중 정확히 하나만 선택하세요.
                새로운 검증 조건을 만들지 마세요.
                threshold, target, unit, reward, damage 관련 값은 절대 변경하지 마세요.
                customTitle과 customDescription만 한국어로 자연스럽게 작성할 수 있습니다.
                적절한 템플릿이 애매하면 가장 안전한 식단 기록 횟수 템플릿을 선택하세요.

                출력 형식:
                {
                  "selectedTemplateId": 1,
                  "customTitle": "string",
                  "customDescription": "string"
                }

                보스전 정보:
                난이도: %s
                활성 참여자 수: %d
                현재 참여자 순번: %d
                현재 참여자 닉네임: %s

                선택 가능한 퀘스트 템플릿:
                %s
                """.formatted(
                prompt.battleDifficulty(),
                prompt.activeMemberCount(),
                prompt.memberIndex(),
                prompt.memberNickname(),
                templates
        ));
    }

    @Override
    public String modelName() {
        return properties.getModel();
    }
}
