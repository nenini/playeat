package com.nyamnyam.coach.boss.service;

import com.nyamnyam.coach.boss.entity.Quest;
import com.nyamnyam.coach.boss.entity.BossDifficulty;
import com.nyamnyam.coach.boss.entity.QuestSourceType;
import com.nyamnyam.coach.boss.entity.QuestStatus;
import com.nyamnyam.coach.boss.entity.QuestType;
import com.nyamnyam.coach.boss.repository.row.QuestBattleRow;
import com.nyamnyam.coach.boss.repository.row.QuestGuildMemberRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.BossErrorCode;
import org.springframework.stereotype.Component;

@Component
public class PlaceholderQuestGenerator implements QuestGenerator {

    private static final int PLACEHOLDER_REWARD_EXP = 30;
    private static final int PLACEHOLDER_REWARD_COIN = 10;

    @Override
    public Quest generatePersonalQuest(
            QuestBattleRow battle,
            QuestGuildMemberRow member,
            int activeMemberCount,
            int memberIndex
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
        quest.setDamage(calculateDamage(battle, activeMemberCount, memberIndex));
        quest.setRewardExp(PLACEHOLDER_REWARD_EXP);
        quest.setRewardCoin(PLACEHOLDER_REWARD_COIN);
        quest.setStatus(QuestStatus.IN_PROGRESS.name());
        quest.setSourceType(QuestSourceType.PLACEHOLDER.name());
        return quest;
    }

    private int calculateDamage(QuestBattleRow battle, int activeMemberCount, int memberIndex) {
        if (activeMemberCount <= 0) {
            return 0;
        }
        BossDifficulty difficulty = parseDifficulty(battle.getDifficulty());
        int personalTotalDamage = difficulty.calculatePersonalTotalDamage(battle.getMaxHp());
        int baseDamage = personalTotalDamage / activeMemberCount;
        int remainder = personalTotalDamage % activeMemberCount;
        return memberIndex < remainder ? baseDamage + 1 : baseDamage;
    }

    private BossDifficulty parseDifficulty(String difficulty) {
        try {
            return BossDifficulty.from(difficulty);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new BusinessException(BossErrorCode.BOSS_DIFFICULTY_INVALID);
        }
    }
}
