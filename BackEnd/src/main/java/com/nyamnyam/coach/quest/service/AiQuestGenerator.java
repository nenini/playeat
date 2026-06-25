package com.nyamnyam.coach.quest.service;

import com.nyamnyam.coach.ai.service.AiTextGenerator;
import com.nyamnyam.coach.ai.service.parser.AiJsonResponseParser;
import com.nyamnyam.coach.ai.service.parser.AiQuestContent;
import com.nyamnyam.coach.ai.service.prompt.AiQuestPrompt;
import com.nyamnyam.coach.ai.service.prompt.QuestTemplatePrompt;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.QuestErrorCode;
import com.nyamnyam.coach.quest.entity.Quest;
import com.nyamnyam.coach.quest.entity.QuestSourceType;
import com.nyamnyam.coach.quest.entity.QuestStatus;
import com.nyamnyam.coach.quest.entity.QuestTemplate;
import com.nyamnyam.coach.quest.repository.QuestRepository;
import com.nyamnyam.coach.quest.repository.QuestTemplateRepository;
import com.nyamnyam.coach.quest.repository.row.QuestBattleRow;
import com.nyamnyam.coach.quest.repository.row.QuestGuildMemberRow;
import com.nyamnyam.coach.quest.repository.row.RecentDietSummaryRow;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "gms", name = "enabled", havingValue = "true")
public class AiQuestGenerator implements QuestGenerator {

    private static final int MAX_CUSTOM_TEXT_LENGTH = 200;
    private static final int RECENT_DAYS = 3;
    private static final BigDecimal DEFAULT_SUGAR_LIMIT = BigDecimal.valueOf(50);
    private static final BigDecimal DEFAULT_SODIUM_LIMIT = BigDecimal.valueOf(2000);
    private static final BigDecimal DEFAULT_FIBER_TARGET = BigDecimal.valueOf(25);

    private final QuestTemplateRepository questTemplateRepository;
    private final QuestRepository questRepository;
    private final AiTextGenerator aiTextGenerator;
    private final AiJsonResponseParser aiJsonResponseParser;

    public AiQuestGenerator(
            QuestTemplateRepository questTemplateRepository,
            QuestRepository questRepository,
            AiTextGenerator aiTextGenerator,
            AiJsonResponseParser aiJsonResponseParser
    ) {
        this.questTemplateRepository = questTemplateRepository;
        this.questRepository = questRepository;
        this.aiTextGenerator = aiTextGenerator;
        this.aiJsonResponseParser = aiJsonResponseParser;
    }

    @Override
    public Quest generatePersonalQuest(
            QuestBattleRow battle,
            QuestGuildMemberRow member,
            int activeMemberCount,
            int memberIndex
    ) {
        List<QuestTemplate> templates = findCandidateTemplates(battle.getDifficulty());
        if (templates.isEmpty()) {
            throw new BusinessException(QuestErrorCode.QUEST_TEMPLATE_NOT_FOUND);
        }
        templates = excludeRecentlyAssignedTemplates(templates, member.getUserId());
        RecentDietSummaryRow recentDietSummary = findRecentDietSummary(member.getUserId());

        AiQuestContent content = aiJsonResponseParser.parseQuest(aiTextGenerator.generateDailyQuest(
                new AiQuestPrompt(
                        battle.getDifficulty(),
                        activeMemberCount,
                        memberIndex,
                        member.getNickname(),
                        toPromptSummary(recentDietSummary),
                        templates.stream().map(this::toPrompt).toList()
                )
        ));

        QuestTemplate selectedTemplate = selectTemplate(templates, content.selectedTemplateId(), recentDietSummary, battle, member, memberIndex);
        boolean useAiText = selectedTemplate.getTemplateId() != null
                && selectedTemplate.getTemplateId().equals(content.selectedTemplateId());
        Quest quest = copyTemplateToQuest(selectedTemplate);
        quest.setBattleId(battle.getBattleId());
        quest.setGuildId(battle.getGuildId());
        quest.setUserId(member.getUserId());
        quest.setTitle(useAiText ? sanitize(content.customTitle(), selectedTemplate.getTitle()) : selectedTemplate.getTitle());
        quest.setDescription(useAiText ? sanitize(content.customDescription(), selectedTemplate.getDescription()) : selectedTemplate.getDescription());
        quest.setCurrentValue(0);
        quest.setDamage(calculateDamage(battle, activeMemberCount, memberIndex));
        quest.setStatus(QuestStatus.IN_PROGRESS.name());
        quest.setSourceType(QuestSourceType.AI.name());
        return quest;
    }

    private List<QuestTemplate> findCandidateTemplates(String difficulty) {
        List<QuestTemplate> difficultyTemplates = questTemplateRepository.findActiveTemplatesByDifficulty(difficulty);
        List<QuestTemplate> allTemplates = questTemplateRepository.findActiveTemplates();
        if (difficultyTemplates.isEmpty()) {
            return allTemplates;
        }
        if (allTemplates.isEmpty()) {
            return difficultyTemplates;
        }

        Map<Long, QuestTemplate> candidates = new LinkedHashMap<>();
        for (QuestTemplate template : difficultyTemplates) {
            candidates.put(template.getTemplateId(), template);
        }
        for (QuestTemplate template : allTemplates) {
            candidates.putIfAbsent(template.getTemplateId(), template);
        }
        return new ArrayList<>(candidates.values());
    }

    private List<QuestTemplate> excludeRecentlyAssignedTemplates(List<QuestTemplate> templates, Long userId) {
        LocalDateTime startAt = LocalDate.now().minusDays(RECENT_DAYS - 1L).atStartOfDay();
        List<Long> recentTemplateIds = questRepository.findRecentQuestTemplateIds(userId, startAt);
        if (recentTemplateIds == null || recentTemplateIds.isEmpty()) {
            return templates;
        }

        List<QuestTemplate> filteredTemplates = templates.stream()
                .filter(template -> template.getTemplateId() == null || !recentTemplateIds.contains(template.getTemplateId()))
                .toList();
        if (filteredTemplates.isEmpty()) {
            return templates;
        }
        return filteredTemplates;
    }

    private RecentDietSummaryRow findRecentDietSummary(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startAt = today.minusDays(RECENT_DAYS - 1L).atStartOfDay();
        LocalDateTime endAt = today.plusDays(1).atStartOfDay();
        return questRepository.findRecentDietSummary(userId, startAt, endAt);
    }

    private QuestTemplate selectTemplate(
            List<QuestTemplate> templates,
            Long selectedTemplateId,
            RecentDietSummaryRow summary,
            QuestBattleRow battle,
            QuestGuildMemberRow member,
            int memberIndex
    ) {
        if (selectedTemplateId == null) {
            return selectFallbackTemplate(templates, summary, battle, member, memberIndex);
        }
        QuestTemplate selectedTemplate = templates.stream()
                .filter(template -> selectedTemplateId.equals(template.getTemplateId()))
                .findFirst()
                .orElse(null);
        if (selectedTemplate == null) {
            return selectFallbackTemplate(templates, summary, battle, member, memberIndex);
        }
        return selectedTemplate;
    }

    private QuestTemplate selectFallbackTemplate(
            List<QuestTemplate> templates,
            RecentDietSummaryRow summary,
            QuestBattleRow battle,
            QuestGuildMemberRow member,
            int memberIndex
    ) {
        if (summary == null || defaultValue(summary.getRecordedDays()) == 0) {
            return findTemplateByMetric(templates, "DIET_RECORD_COUNT").orElse(templates.get(0));
        }
        if (isGreaterThan(summary.getAvgSodiumMg(), DEFAULT_SODIUM_LIMIT)) {
            return findTemplateByMetric(templates, "SODIUM").orElse(deterministicTemplate(templates, battle, member, memberIndex));
        }
        if (isGreaterThan(summary.getAvgSugarG(), DEFAULT_SUGAR_LIMIT)) {
            return findTemplateByMetric(templates, "SUGAR").orElse(deterministicTemplate(templates, battle, member, memberIndex));
        }
        if (isLessThanRatio(summary.getAvgProteinG(), summary.getTargetProteinG(), BigDecimal.valueOf(70))) {
            return findTemplateByMetric(templates, "PROTEIN").orElse(deterministicTemplate(templates, battle, member, memberIndex));
        }
        if (isLessThanRatio(summary.getAvgFiberG(), DEFAULT_FIBER_TARGET, BigDecimal.valueOf(70))) {
            return findTemplateByMetric(templates, "FIBER").orElse(deterministicTemplate(templates, battle, member, memberIndex));
        }
        return deterministicTemplate(templates, battle, member, memberIndex);
    }

    private java.util.Optional<QuestTemplate> findTemplateByMetric(List<QuestTemplate> templates, String metricType) {
        return templates.stream()
                .filter(template -> metricType.equals(template.getMetricType()))
                .findFirst();
    }

    private QuestTemplate deterministicTemplate(
            List<QuestTemplate> templates,
            QuestBattleRow battle,
            QuestGuildMemberRow member,
            int memberIndex
    ) {
        if (templates.size() == 1) {
            return templates.get(0);
        }
        long battleId = battle.getBattleId() == null ? 0 : battle.getBattleId();
        long userId = member.getUserId() == null ? 0 : member.getUserId();
        int index = Math.floorMod((int) (battleId * 31 + userId * 17 + memberIndex), templates.size());
        return templates.get(index);
    }

    private QuestTemplatePrompt toPrompt(QuestTemplate template) {
        return new QuestTemplatePrompt(
                template.getTemplateId(),
                template.getTitle(),
                template.getDescription(),
                template.getQuestType(),
                template.getConditionCategory(),
                template.getMetricType(),
                template.getComparisonType(),
                template.getAggregationType(),
                template.getEvaluationScope(),
                template.getThresholdValue(),
                template.getThresholdMinValue(),
                template.getThresholdMaxValue(),
                template.getThresholdUnit(),
                template.getTargetValue(),
                template.getUnit()
        );
    }

    private String toPromptSummary(RecentDietSummaryRow summary) {
        if (summary == null || defaultValue(summary.getRecordedDays()) == 0) {
            return "최근 3일 식단 기록이 없습니다. 식단 기록 습관을 만드는 퀘스트를 우선 고려하세요.";
        }
        return """
                최근 3일 중 기록일수: %d일
                식사 패턴: 아침 %d일, 점심 %d일, 저녁 %d일, 간식 %d일
                일평균 칼로리: %s kcal / 목표 %s kcal
                일평균 단백질: %s g / 목표 %s g
                일평균 탄수화물: %s g / 목표 %s g
                일평균 지방: %s g / 목표 %s g
                일평균 당류: %s g / 권장 상한 %s g
                일평균 나트륨: %s mg / 권장 상한 %s mg
                일평균 식이섬유: %s g / 권장 목표 %s g
                """.formatted(
                defaultValue(summary.getRecordedDays()),
                defaultValue(summary.getBreakfastDays()),
                defaultValue(summary.getLunchDays()),
                defaultValue(summary.getDinnerDays()),
                defaultValue(summary.getSnackDays()),
                display(summary.getAvgCalories()),
                display(summary.getTargetCalories()),
                display(summary.getAvgProteinG()),
                display(summary.getTargetProteinG()),
                display(summary.getAvgCarbsG()),
                display(summary.getTargetCarbsG()),
                display(summary.getAvgFatG()),
                display(summary.getTargetFatG()),
                display(summary.getAvgSugarG()),
                display(DEFAULT_SUGAR_LIMIT),
                display(summary.getAvgSodiumMg()),
                display(DEFAULT_SODIUM_LIMIT),
                display(summary.getAvgFiberG()),
                display(DEFAULT_FIBER_TARGET)
        );
    }

    private Quest copyTemplateToQuest(QuestTemplate template) {
        Quest quest = new Quest();
        quest.setQuestTemplateId(template.getTemplateId());
        quest.setTitle(template.getTitle());
        quest.setDescription(template.getDescription());
        quest.setQuestType(template.getQuestType());
        quest.setConditionCategory(template.getConditionCategory());
        quest.setMetricType(template.getMetricType());
        quest.setComparisonType(template.getComparisonType());
        quest.setAggregationType(template.getAggregationType());
        quest.setEvaluationScope(template.getEvaluationScope());
        quest.setThresholdValue(template.getThresholdValue());
        quest.setThresholdMinValue(template.getThresholdMinValue());
        quest.setThresholdMaxValue(template.getThresholdMaxValue());
        quest.setThresholdUnit(template.getThresholdUnit());
        quest.setTargetValue(template.getTargetValue());
        quest.setUnit(template.getUnit());
        quest.setRewardExp(template.getRewardExp());
        quest.setRewardCoin(template.getRewardCoin());
        return quest;
    }

    private int calculateDamage(QuestBattleRow battle, int activeMemberCount, int memberIndex) {
        if (battle == null || battle.getMaxHp() == null || activeMemberCount <= 0) {
            return 0;
        }

        double personalRatio = getPersonalQuestRatio(battle.getDifficulty());
        int personalTotalDamage = (int) Math.round(battle.getMaxHp() * personalRatio);
        int baseDamage = personalTotalDamage / activeMemberCount;
        int remainder = personalTotalDamage % activeMemberCount;

        return memberIndex < remainder ? baseDamage + 1 : baseDamage;
    }

    private double getPersonalQuestRatio(String difficulty) {
        if ("NORMAL".equals(difficulty)) {
            return 0.7;
        }
        if ("HARD".equals(difficulty)) {
            return 0.6;
        }
        return 0.8;
    }

    private boolean isGreaterThan(BigDecimal value, BigDecimal threshold) {
        return value != null && threshold != null && value.compareTo(threshold) > 0;
    }

    private boolean isLessThanRatio(BigDecimal value, BigDecimal target, BigDecimal ratioPercent) {
        if (value == null || target == null || target.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        BigDecimal ratio = value.multiply(BigDecimal.valueOf(100))
                .divide(target, 2, RoundingMode.HALF_UP);
        return ratio.compareTo(ratioPercent) < 0;
    }

    private int defaultValue(Integer value) {
        return value == null ? 0 : value;
    }

    private String display(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.setScale(1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private String sanitize(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        String compact = value.replaceAll("[\\r\\n\\t]+", " ").trim();
        if (compact.length() > MAX_CUSTOM_TEXT_LENGTH) {
            return compact.substring(0, MAX_CUSTOM_TEXT_LENGTH);
        }
        return compact;
    }
}
