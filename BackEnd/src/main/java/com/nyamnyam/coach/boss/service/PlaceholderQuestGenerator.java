package com.nyamnyam.coach.boss.service;

import com.nyamnyam.coach.boss.entity.Quest;
import com.nyamnyam.coach.boss.entity.QuestSourceType;
import com.nyamnyam.coach.boss.entity.QuestStatus;
import com.nyamnyam.coach.boss.entity.QuestTemplate;
import com.nyamnyam.coach.boss.repository.QuestTemplateRepository;
import com.nyamnyam.coach.boss.repository.row.QuestBattleRow;
import com.nyamnyam.coach.boss.repository.row.QuestGuildMemberRow;
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
        quest.setDamage(template.getDamage());
        quest.setRewardExp(template.getRewardExp());
        quest.setRewardCoin(template.getRewardCoin());
        quest.setStatus(QuestStatus.IN_PROGRESS.name());
        quest.setSourceType(QuestSourceType.PLACEHOLDER.name());
        return quest;
    }
}
