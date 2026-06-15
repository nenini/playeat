package com.nyamnyam.coach.quest.service;

import com.nyamnyam.coach.quest.entity.Quest;
import com.nyamnyam.coach.quest.entity.QuestSourceType;
import com.nyamnyam.coach.quest.entity.QuestStatus;
import com.nyamnyam.coach.quest.entity.QuestTemplate;
import com.nyamnyam.coach.quest.repository.QuestTemplateRepository;
import com.nyamnyam.coach.quest.repository.row.QuestBattleRow;
import com.nyamnyam.coach.quest.repository.row.QuestGuildMemberRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.QuestErrorCode;
import org.springframework.stereotype.Component;

@Component
public class PlaceholderQuestGenerator implements QuestGenerator {

    private final QuestTemplateRepository questTemplateRepository;

    public PlaceholderQuestGenerator(QuestTemplateRepository questTemplateRepository) {
        this.questTemplateRepository = questTemplateRepository;
    }

    @Override
    public Quest generatePersonalQuest(
            QuestBattleRow battle,
            QuestGuildMemberRow member,
            int activeMemberCount,
            int memberIndex
    ) {
        QuestTemplate template = questTemplateRepository.findDefaultTemplate()
                .orElseThrow(() -> new BusinessException(QuestErrorCode.QUEST_TEMPLATE_NOT_FOUND));
        Quest quest = new Quest();
        quest.setBattleId(battle.getBattleId());
        quest.setGuildId(battle.getGuildId());
        quest.setUserId(member.getUserId());
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
        quest.setCurrentValue(0);
        quest.setUnit(template.getUnit());
        quest.setDamage(calculateDamage(battle, activeMemberCount, memberIndex));        quest.setRewardExp(template.getRewardExp());
        quest.setRewardCoin(template.getRewardCoin());
        quest.setStatus(QuestStatus.IN_PROGRESS.name());
        quest.setSourceType(QuestSourceType.PLACEHOLDER.name());
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
}
