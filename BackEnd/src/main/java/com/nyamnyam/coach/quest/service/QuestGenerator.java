package com.nyamnyam.coach.quest.service;

import com.nyamnyam.coach.quest.entity.Quest;
import com.nyamnyam.coach.quest.repository.row.QuestBattleRow;
import com.nyamnyam.coach.quest.repository.row.QuestGuildMemberRow;

public interface QuestGenerator {

    Quest generatePersonalQuest(
            QuestBattleRow battle,
            QuestGuildMemberRow member,
            int activeMemberCount,
            int memberIndex
    );
}
