package com.nyamnyam.coach.ai.service;

import com.nyamnyam.coach.ai.client.GmsAiClient;
import com.nyamnyam.coach.ai.config.GmsProperties;
import com.nyamnyam.coach.ai.rag.document.RagReference;
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
                당신은 사용자가 선택한 캐릭터형 식단 코치입니다.
                반드시 JSON만 반환하고 마크다운은 사용하지 마세요.
                질병을 진단하지 말고, 현재 식단 기록을 바탕으로 실천 가능한 한마디 피드백만 작성하세요.
                응답은 반드시 선택한 코치의 말투를 반영하세요.

                출력 형식:
                {"message":"string"}

                코치 정보:
                이름: %s
                말투: %s

                식사 영양 정보:
                칼로리: %s kcal
                단백질: %s g
                탄수화물: %s g
                지방: %s g
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
                반드시 JSON만 반환하고 마크다운은 사용하지 마세요.
                음식명과 섭취량을 반영해서 구체적으로 말하되 의학적 진단처럼 쓰지 마세요.
                사용자가 바로 읽을 수 있는 짧고 실행 가능한 문장으로 작성하세요.

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
        String ragReferences = prompt.ragReferences().isEmpty()
                ? "검색된 공식 건강 자료 없음"
                : prompt.ragReferences().stream()
                .map(this::toRagReferenceText)
                .reduce((left, right) -> left + "\n" + right)
                .orElse("검색된 공식 건강 자료 없음");

        return gmsAiClient.createResponse("""
                당신은 공식 건강 자료와 사용자의 실제 식단 기록을 함께 참고해 주간 식습관 리포트를 작성하는 한국어 영양 코치입니다.
                반드시 JSON만 반환하고 마크다운은 사용하지 마세요.
                질병을 진단하거나 치료를 지시하지 마세요.
                공식 자료는 일반적인 건강 정보로만 사용하고, 사용자의 기록과 건강 목표에 맞는 실천 가능한 조언을 작성하세요.
                알레르기나 피해야 하는 식품이 있으면 그 식품을 추천하지 마세요.

                출력 규칙:
                - summary: 2~3문장
                - strengths: 2개
                - warnings: 2개
                - nextAction: 다음 주에 할 수 있는 행동 1~2문장

                출력 형식:
                {
                  "summary": "string",
                  "strengths": ["string", "string"],
                  "warnings": ["string", "string"],
                  "nextAction": "string"
                }

                주간 기간:
                시작일: %s
                종료일: %s
                평균 건강 점수: %d

                사용자 건강 프로필:
                %s

                일별 식단 요약:
                %s

                일별 영양 분석 요약:
                %s

                반복 패턴:
                %s

                RAG 공식 건강 자료:
                %s
                """.formatted(
                prompt.startDate(),
                prompt.endDate(),
                prompt.averageHealthScore(),
                prompt.healthProfileSummary(),
                String.join("\n", prompt.dailyMealSummaries()),
                String.join("\n", prompt.dailyNutritionSummaries()),
                String.join("\n", prompt.repeatedPatterns()),
                ragReferences
        ));
    }

    private String toRagReferenceText(RagReference reference) {
        return "- [%s] %s (%s): %s".formatted(
                reference.sourceName(),
                reference.documentTitle(),
                reference.topic(),
                reference.content()
        );
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
                당신은 보스전 참여자에게 줄 개인 퀘스트 템플릿을 선택하는 한국어 영양 코치입니다.
                반드시 JSON만 반환하고 마크다운은 사용하지 마세요.
                새 퀘스트 조건을 만들지 말고 availableQuestTemplates에 있는 templateId 중 정확히 하나만 선택하세요.
                threshold, target, unit, reward, damage 값은 바꾸지 마세요.
                customTitle과 customDescription만 선택한 템플릿 조건에 맞게 자연스럽게 작성하세요.

                선택 기준:
                1. 최근 식단 요약에서 반복적으로 부족하거나 초과된 영양소와 직접 연결된 템플릿을 우선 선택하세요.
                2. 나트륨/당류 과다는 줄이는 템플릿을 우선 고려하세요.
                3. 단백질/식이섬유 부족은 보완 템플릿을 고려하세요.
                4. 식단 기록 일수가 부족하면 기록형 또는 식사 패턴형 템플릿을 고려하세요.
                5. 판단이 어렵다면 가장 쉬운 템플릿을 선택하세요.

                출력 형식:
                {
                  "selectedTemplateId": 1,
                  "selectionReason": "string",
                  "customTitle": "string",
                  "customDescription": "string"
                }

                보스전 정보:
                난이도: %s
                활성 참여자 수: %d
                현재 참여자 순번: %d
                현재 참여자 닉네임: %s

                최근 식단 요약:
                %s

                선택 가능한 퀘스트 템플릿:
                %s
                """.formatted(
                prompt.battleDifficulty(),
                prompt.activeMemberCount(),
                prompt.memberIndex(),
                prompt.memberNickname(),
                prompt.recentDietSummary(),
                templates
        ));
    }

    @Override
    public String modelName() {
        return properties.getModel();
    }
}
