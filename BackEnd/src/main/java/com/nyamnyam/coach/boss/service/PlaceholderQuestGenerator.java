package com.nyamnyam.coach.boss.service;

import com.nyamnyam.coach.boss.entity.Quest;
import com.nyamnyam.coach.boss.entity.QuestSourceType;
import com.nyamnyam.coach.boss.entity.QuestStatus;
import com.nyamnyam.coach.boss.entity.QuestType;
import com.nyamnyam.coach.boss.repository.row.QuestBattleRow;
import com.nyamnyam.coach.boss.repository.row.QuestGuildMemberRow;
import org.springframework.stereotype.Component;

@Component
public class PlaceholderQuestGenerator implements QuestGenerator {

    private static final int PLACEHOLDER_DAMAGE = 100;
    private static final int PLACEHOLDER_REWARD_EXP = 30;
    private static final int PLACEHOLDER_REWARD_COIN = 10;

    @Override
    public Quest generatePersonalQuest(
            QuestBattleRow battle,
            QuestGuildMemberRow member,
            int activeMemberCount
    ) {
        Quest quest = new Quest();
        quest.setBattleId(battle.getBattleId());
        quest.setGuildId(battle.getGuildId());
        quest.setUserId(member.getUserId());
        quest.setTitle("오늘 식단 기록하기");
        quest.setDescription("오늘 하루 식단을 1회 이상 기록하세요.");
        quest.setQuestType(QuestType.RECORD_DIET.name());
        quest.setTargetValue(1);
        quest.setCurrentValue(0);
        quest.setUnit("회");
        quest.setDamage(calculateDamage(battle, activeMemberCount));
        quest.setRewardExp(PLACEHOLDER_REWARD_EXP);
        quest.setRewardCoin(PLACEHOLDER_REWARD_COIN);
        quest.setStatus(QuestStatus.IN_PROGRESS.name());
        quest.setSourceType(QuestSourceType.PLACEHOLDER.name());
        return quest;
    }

    private int calculateDamage(QuestBattleRow battle, int activeMemberCount) {
        return PLACEHOLDER_DAMAGE;
    }
}
