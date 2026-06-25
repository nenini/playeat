package com.nyamnyam.coach.quest.dto.response;

import java.util.List;

public record BossBattleQuestListResponse(
        Long battleId,
        Long guildId,
        List<QuestSummaryResponse> quests
) {
}
