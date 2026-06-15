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
import com.nyamnyam.coach.quest.repository.QuestTemplateRepository;
import com.nyamnyam.coach.quest.repository.row.QuestBattleRow;
import com.nyamnyam.coach.quest.repository.row.QuestGuildMemberRow;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(prefix = "gms", name = "enabled", havingValue = "true")
public class AiQuestGenerator implements QuestGenerator {

    private static final int MAX_CUSTOM_TEXT_LENGTH = 200;

    private final QuestTemplateRepository questTemplateRepository;
    private final AiTextGenerator aiTextGenerator;
    private final AiJsonResponseParser aiJsonResponseParser;

    public AiQuestGenerator(
            QuestTemplateRepository questTemplateRepository,
            AiTextGenerator aiTextGenerator,
            AiJsonResponseParser aiJsonResponseParser
    ) {
        this.questTemplateRepository = questTemplateRepository;
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
        List<QuestTemplate> templates = questTemplateRepository.findActiveTemplatesByDifficulty(battle.getDifficulty());
        if (templates.isEmpty()) {
            templates = questTemplateRepository.findActiveTemplates();
        }
        if (templates.isEmpty()) {
            throw new BusinessException(QuestErrorCode.QUEST_TEMPLATE_NOT_FOUND);
        }

        AiQuestContent content = aiJsonResponseParser.parseQuest(aiTextGenerator.generateDailyQuest(
                new AiQuestPrompt(
                        battle.getDifficulty(),
                        activeMemberCount,
                        memberIndex,
                        member.getNickname(),
                        templates.stream().map(this::toPrompt).toList()
                )
        ));

        QuestTemplate selectedTemplate = selectTemplate(templates, content.selectedTemplateId());
        Quest quest = copyTemplateToQuest(selectedTemplate);
        quest.setBattleId(battle.getBattleId());
        quest.setGuildId(battle.getGuildId());
        quest.setUserId(member.getUserId());
        quest.setTitle(sanitize(content.customTitle(), selectedTemplate.getTitle()));
        quest.setDescription(sanitize(content.customDescription(), selectedTemplate.getDescription()));
        quest.setCurrentValue(0);
        quest.setDamage(calculateDamage(battle, activeMemberCount, memberIndex));
        quest.setStatus(QuestStatus.IN_PROGRESS.name());
        quest.setSourceType(QuestSourceType.AI.name());
        return quest;
    }

    private QuestTemplate selectTemplate(List<QuestTemplate> templates, Long selectedTemplateId) {
        if (selectedTemplateId == null) {
            return templates.get(0);
        }
        return templates.stream()
                .filter(template -> selectedTemplateId.equals(template.getTemplateId()))
                .findFirst()
                .orElse(templates.get(0));
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
