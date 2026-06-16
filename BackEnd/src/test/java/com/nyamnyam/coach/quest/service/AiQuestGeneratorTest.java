package com.nyamnyam.coach.quest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.ai.service.AiTextGenerator;
import com.nyamnyam.coach.ai.service.parser.AiJsonResponseParser;
import com.nyamnyam.coach.ai.service.prompt.AiQuestPrompt;
import com.nyamnyam.coach.ai.service.prompt.CoachFeedbackPrompt;
import com.nyamnyam.coach.ai.service.prompt.DailyReportPrompt;
import com.nyamnyam.coach.ai.service.prompt.WeeklyReportPrompt;
import com.nyamnyam.coach.quest.entity.Quest;
import com.nyamnyam.coach.quest.entity.QuestTemplate;
import com.nyamnyam.coach.quest.repository.QuestRepository;
import com.nyamnyam.coach.quest.repository.QuestTemplateRepository;
import com.nyamnyam.coach.quest.repository.row.QuestBattleRow;
import com.nyamnyam.coach.quest.repository.row.QuestGuildMemberRow;
import com.nyamnyam.coach.quest.repository.row.RecentDietSummaryRow;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiQuestGeneratorTest {

    @Test
    void generatePersonalQuestUsesSelectedTemplateAndRecentDietSummaryPrompt() {
        QuestTemplate recordTemplate = template(1L, "Record diet", "Record one diet", "DIET_RECORD_COUNT");
        QuestTemplate proteinTemplate = template(2L, "Eat protein", "Eat enough protein", "PROTEIN");
        CapturingAiTextGenerator aiTextGenerator = new CapturingAiTextGenerator("""
                {
                  "selectedTemplateId": 2,
                  "selectionReason": "Protein has been low recently.",
                  "customTitle": "Protein boost quest",
                  "customDescription": "Reach today's protein target."
                }
                """);
        AiQuestGenerator generator = new AiQuestGenerator(
                new FakeQuestTemplateRepository(List.of(recordTemplate, proteinTemplate)),
                recentDietRepository(summaryWithLowProtein()),
                aiTextGenerator,
                new AiJsonResponseParser(new ObjectMapper())
        );

        Quest quest = generator.generatePersonalQuest(battle("NORMAL", 1000), member(11L, "tester"), 3, 1);

        assertThat(aiTextGenerator.lastPrompt.recentDietSummary()).contains("최근 7일 중 기록일수: 5일");
        assertThat(aiTextGenerator.lastPrompt.recentDietSummary()).contains("일평균 단백질: 35 g / 목표 70 g");
        assertThat(quest.getQuestTemplateId()).isEqualTo(2L);
        assertThat(quest.getTitle()).isEqualTo("Protein boost quest");
        assertThat(quest.getDescription()).isEqualTo("Reach today's protein target.");
        assertThat(quest.getConditionCategory()).isEqualTo("NUTRITION");
        assertThat(quest.getMetricType()).isEqualTo("PROTEIN");
        assertThat(quest.getComparisonType()).isEqualTo("GREATER_THAN_OR_EQUAL");
        assertThat(quest.getAggregationType()).isEqualTo("DAILY_VALUE");
        assertThat(quest.getEvaluationScope()).isEqualTo("USER_DAILY");
        assertThat(quest.getThresholdValue()).isEqualByComparingTo("60.00");
        assertThat(quest.getDamage()).isEqualTo(233);
        assertThat(quest.getSourceType()).isEqualTo("AI");
        assertThat(quest.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void generatePersonalQuestExcludesRecentlyAssignedTemplateFromAiCandidates() {
        QuestTemplate recordTemplate = template(1L, "Record diet", "Record one diet", "DIET_RECORD_COUNT");
        QuestTemplate proteinTemplate = template(2L, "Eat protein", "Eat enough protein", "PROTEIN");
        CapturingAiTextGenerator aiTextGenerator = new CapturingAiTextGenerator("""
                {
                  "selectedTemplateId": 2,
                  "selectionReason": "Record template was recently assigned.",
                  "customTitle": "Protein boost quest",
                  "customDescription": "Reach today's protein target."
                }
                """);
        AiQuestGenerator generator = new AiQuestGenerator(
                new FakeQuestTemplateRepository(List.of(recordTemplate, proteinTemplate)),
                recentDietRepository(summaryWithLowProtein(), List.of(1L)),
                aiTextGenerator,
                new AiJsonResponseParser(new ObjectMapper())
        );

        Quest quest = generator.generatePersonalQuest(battle("NORMAL", 1000), member(11L, "tester"), 3, 1);

        assertThat(aiTextGenerator.lastPrompt.availableQuestTemplates())
                .extracting("templateId")
                .containsExactly(2L);
        assertThat(quest.getQuestTemplateId()).isEqualTo(2L);
    }

    @Test
    void generatePersonalQuestFallsBackByRecentDietWhenAiSelectsUnknownTemplate() {
        QuestTemplate recordTemplate = template(1L, "Record diet", "Record one diet", "DIET_RECORD_COUNT");
        QuestTemplate sodiumTemplate = template(2L, "Reduce sodium", "Keep sodium low", "SODIUM");
        AiQuestGenerator generator = new AiQuestGenerator(
                new FakeQuestTemplateRepository(List.of(recordTemplate, sodiumTemplate)),
                recentDietRepository(summaryWithHighSodium()),
                new CapturingAiTextGenerator("""
                        {
                          "selectedTemplateId": 999,
                          "selectionReason": "invalid id",
                          "customTitle": "Wrong template",
                          "customDescription": "Wrong description"
                        }
                        """),
                new AiJsonResponseParser(new ObjectMapper())
        );

        Quest quest = generator.generatePersonalQuest(battle("EASY", 1000), member(11L, "tester"), 2, 0);

        assertThat(quest.getQuestTemplateId()).isEqualTo(2L);
        assertThat(quest.getTitle()).isEqualTo("Reduce sodium");
        assertThat(quest.getDescription()).isEqualTo("Keep sodium low");
        assertThat(quest.getMetricType()).isEqualTo("SODIUM");
        assertThat(quest.getDamage()).isEqualTo(400);
    }

    @Test
    void generatePersonalQuestFallsBackToDietRecordWhenNoRecentDietExists() {
        QuestTemplate recordTemplate = template(1L, "Record diet", "Record one diet", "DIET_RECORD_COUNT");
        QuestTemplate proteinTemplate = template(2L, "Eat protein", "Eat enough protein", "PROTEIN");
        AiQuestGenerator generator = new AiQuestGenerator(
                new FakeQuestTemplateRepository(List.of(recordTemplate, proteinTemplate)),
                recentDietRepository(new RecentDietSummaryRow()),
                new CapturingAiTextGenerator("""
                        {
                          "selectedTemplateId": null,
                          "selectionReason": "",
                          "customTitle": "",
                          "customDescription": ""
                        }
                        """),
                new AiJsonResponseParser(new ObjectMapper())
        );

        Quest quest = generator.generatePersonalQuest(battle("EASY", 1000), member(11L, "tester"), 2, 0);

        assertThat(quest.getQuestTemplateId()).isEqualTo(1L);
        assertThat(quest.getMetricType()).isEqualTo("DIET_RECORD_COUNT");
    }

    private QuestRepository recentDietRepository(RecentDietSummaryRow summary) {
        return recentDietRepository(summary, List.of());
    }

    private QuestRepository recentDietRepository(RecentDietSummaryRow summary, List<Long> recentTemplateIds) {
        QuestRepository repository = mock(QuestRepository.class);
        when(repository.findRecentDietSummary(eq(11L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(summary);
        when(repository.findRecentQuestTemplateIds(eq(11L), any(LocalDateTime.class)))
                .thenReturn(recentTemplateIds);
        return repository;
    }

    private RecentDietSummaryRow summaryWithLowProtein() {
        RecentDietSummaryRow row = new RecentDietSummaryRow();
        row.setRecordedDays(5);
        row.setBreakfastDays(2);
        row.setLunchDays(5);
        row.setDinnerDays(4);
        row.setSnackDays(1);
        row.setAvgCalories(new BigDecimal("1600"));
        row.setAvgProteinG(new BigDecimal("35"));
        row.setAvgCarbsG(new BigDecimal("210"));
        row.setAvgFatG(new BigDecimal("45"));
        row.setAvgSugarG(new BigDecimal("30"));
        row.setAvgSodiumMg(new BigDecimal("1800"));
        row.setAvgFiberG(new BigDecimal("18"));
        row.setTargetCalories(new BigDecimal("2000"));
        row.setTargetProteinG(new BigDecimal("70"));
        row.setTargetCarbsG(new BigDecimal("250"));
        row.setTargetFatG(new BigDecimal("60"));
        return row;
    }

    private RecentDietSummaryRow summaryWithHighSodium() {
        RecentDietSummaryRow row = summaryWithLowProtein();
        row.setAvgProteinG(new BigDecimal("70"));
        row.setAvgSodiumMg(new BigDecimal("2600"));
        return row;
    }

    private QuestBattleRow battle(String difficulty, int maxHp) {
        QuestBattleRow row = new QuestBattleRow();
        row.setBattleId(1L);
        row.setGuildId(2L);
        row.setDifficulty(difficulty);
        row.setMaxHp(maxHp);
        return row;
    }

    private QuestGuildMemberRow member(Long userId, String nickname) {
        QuestGuildMemberRow row = new QuestGuildMemberRow();
        row.setUserId(userId);
        row.setNickname(nickname);
        return row;
    }

    private QuestTemplate template(Long templateId, String title, String description, String metricType) {
        QuestTemplate template = new QuestTemplate();
        template.setTemplateId(templateId);
        template.setTitle(title);
        template.setDescription(description);
        template.setQuestType("DAILY");
        template.setConditionCategory(conditionCategory(metricType));
        template.setMetricType(metricType);
        template.setComparisonType(comparisonType(metricType));
        template.setAggregationType("DIET_RECORD_COUNT".equals(metricType) ? "DAILY_COUNT" : "DAILY_VALUE");
        template.setEvaluationScope("USER_DAILY");
        template.setThresholdValue(threshold(metricType));
        template.setThresholdUnit(thresholdUnit(metricType));
        template.setTargetValue(1);
        template.setUnit("DAY");
        template.setRewardExp(30);
        template.setRewardCoin(10);
        return template;
    }

    private String conditionCategory(String metricType) {
        return "DIET_RECORD_COUNT".equals(metricType) ? "DIET_RECORD" : "NUTRITION";
    }

    private String comparisonType(String metricType) {
        if ("SODIUM".equals(metricType)) {
            return "LESS_THAN_OR_EQUAL";
        }
        return "GREATER_THAN_OR_EQUAL";
    }

    private BigDecimal threshold(String metricType) {
        if ("PROTEIN".equals(metricType)) {
            return new BigDecimal("60.00");
        }
        if ("SODIUM".equals(metricType)) {
            return new BigDecimal("2000.00");
        }
        return BigDecimal.ONE;
    }

    private String thresholdUnit(String metricType) {
        if ("PROTEIN".equals(metricType)) {
            return "GRAM";
        }
        if ("SODIUM".equals(metricType)) {
            return "MG";
        }
        return "COUNT";
    }

    private static class FakeQuestTemplateRepository implements QuestTemplateRepository {
        private final List<QuestTemplate> templates;

        private FakeQuestTemplateRepository(List<QuestTemplate> templates) {
            this.templates = templates;
        }

        @Override
        public List<QuestTemplate> findActiveTemplates() {
            return templates;
        }

        @Override
        public List<QuestTemplate> findActiveTemplatesByDifficulty(String difficulty) {
            return templates;
        }

        @Override
        public Optional<QuestTemplate> findTemplateById(Long templateId) {
            return templates.stream()
                    .filter(template -> templateId.equals(template.getTemplateId()))
                    .findFirst();
        }

        @Override
        public Optional<QuestTemplate> findDefaultTemplate() {
            return templates.stream().findFirst();
        }
    }

    private static class CapturingAiTextGenerator implements AiTextGenerator {
        private final String questResponse;
        private AiQuestPrompt lastPrompt;

        private CapturingAiTextGenerator(String questResponse) {
            this.questResponse = questResponse;
        }

        @Override
        public String generateCoachFeedback(CoachFeedbackPrompt prompt) {
            return "{}";
        }

        @Override
        public String generateDailyReport(DailyReportPrompt prompt) {
            return "{}";
        }

        @Override
        public String generateWeeklyReport(WeeklyReportPrompt prompt) {
            return "{}";
        }

        @Override
        public String generateDailyQuest(AiQuestPrompt prompt) {
            this.lastPrompt = prompt;
            return questResponse;
        }

        @Override
        public String modelName() {
            return "fake";
        }
    }
}
