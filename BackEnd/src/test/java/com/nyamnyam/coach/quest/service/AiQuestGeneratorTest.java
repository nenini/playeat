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
import com.nyamnyam.coach.quest.repository.QuestTemplateRepository;
import com.nyamnyam.coach.quest.repository.row.QuestBattleRow;
import com.nyamnyam.coach.quest.repository.row.QuestGuildMemberRow;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AiQuestGeneratorTest {

    @Test
    void generatePersonalQuestUsesSelectedTemplateAndCopiesConditionSnapshot() {
        QuestTemplate recordTemplate = template(1L, "기록하기", "식단 기록", "DIET_RECORD_COUNT");
        QuestTemplate sugarTemplate = template(2L, "당류 줄이기", "당류 제한", "SUGAR");
        AiQuestGenerator generator = new AiQuestGenerator(
                new FakeQuestTemplateRepository(List.of(recordTemplate, sugarTemplate)),
                new FakeAiTextGenerator("""
                        {
                          "selectedTemplateId": 2,
                          "customTitle": "당류 방어 퀘스트",
                          "customDescription": "오늘은 당류를 낮게 유지해요."
                        }
                        """),
                new AiJsonResponseParser(new ObjectMapper())
        );

        Quest quest = generator.generatePersonalQuest(battle("NORMAL", 1000), member(11L, "예린"), 3, 1);

        assertThat(quest.getQuestTemplateId()).isEqualTo(2L);
        assertThat(quest.getTitle()).isEqualTo("당류 방어 퀘스트");
        assertThat(quest.getDescription()).isEqualTo("오늘은 당류를 낮게 유지해요.");
        assertThat(quest.getConditionCategory()).isEqualTo("NUTRITION");
        assertThat(quest.getMetricType()).isEqualTo("SUGAR");
        assertThat(quest.getComparisonType()).isEqualTo("LESS_THAN_OR_EQUAL");
        assertThat(quest.getAggregationType()).isEqualTo("DAILY_VALUE");
        assertThat(quest.getEvaluationScope()).isEqualTo("USER_DAILY");
        assertThat(quest.getThresholdValue()).isEqualByComparingTo("50.00");
        assertThat(quest.getTargetValue()).isEqualTo(50);
        assertThat(quest.getDamage()).isEqualTo(233);
        assertThat(quest.getSourceType()).isEqualTo("AI");
        assertThat(quest.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void generatePersonalQuestFallsBackToFirstTemplateWhenAiSelectsUnknownTemplate() {
        QuestTemplate fallbackTemplate = template(1L, "기록하기", "식단 기록", "DIET_RECORD_COUNT");
        QuestTemplate sugarTemplate = template(2L, "당류 줄이기", "당류 제한", "SUGAR");
        AiQuestGenerator generator = new AiQuestGenerator(
                new FakeQuestTemplateRepository(List.of(fallbackTemplate, sugarTemplate)),
                new FakeAiTextGenerator("""
                        {
                          "selectedTemplateId": 999,
                          "customTitle": "",
                          "customDescription": ""
                        }
                        """),
                new AiJsonResponseParser(new ObjectMapper())
        );

        Quest quest = generator.generatePersonalQuest(battle("EASY", 1000), member(11L, "예린"), 2, 0);

        assertThat(quest.getQuestTemplateId()).isEqualTo(1L);
        assertThat(quest.getTitle()).isEqualTo("기록하기");
        assertThat(quest.getDescription()).isEqualTo("식단 기록");
        assertThat(quest.getDamage()).isEqualTo(400);
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
        template.setConditionCategory("SUGAR".equals(metricType) ? "NUTRITION" : "DIET_RECORD");
        template.setMetricType(metricType);
        template.setComparisonType("SUGAR".equals(metricType) ? "LESS_THAN_OR_EQUAL" : "GREATER_THAN_OR_EQUAL");
        template.setAggregationType("SUGAR".equals(metricType) ? "DAILY_VALUE" : "DAILY_COUNT");
        template.setEvaluationScope("USER_DAILY");
        template.setThresholdValue(new BigDecimal("50.00"));
        template.setThresholdUnit("GRAM");
        template.setTargetValue(50);
        template.setUnit("GRAM");
        template.setRewardExp(30);
        template.setRewardCoin(10);
        return template;
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

    private static class FakeAiTextGenerator implements AiTextGenerator {
        private final String questResponse;

        private FakeAiTextGenerator(String questResponse) {
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
            return questResponse;
        }

        @Override
        public String modelName() {
            return "fake";
        }
    }
}
