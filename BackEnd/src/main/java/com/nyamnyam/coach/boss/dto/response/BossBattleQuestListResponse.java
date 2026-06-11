package com.nyamnyam.coach.boss.dto.response;

import java.util.List;

public record BossBattleQuestListResponse(
        Long battleId,
        Long guildId,
        List<QuestSummaryResponse> quests
) {
}
