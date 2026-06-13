package com.nyamnyam.coach.boss.service;

import com.nyamnyam.coach.boss.entity.Quest;
import com.nyamnyam.coach.boss.repository.row.QuestBattleRow;
import com.nyamnyam.coach.boss.repository.row.QuestGuildMemberRow;

public interface QuestGenerator {

    Quest generatePersonalQuest(
            QuestBattleRow battle,
            QuestGuildMemberRow member,
            int activeMemberCount,
            int memberIndex
    );
}
