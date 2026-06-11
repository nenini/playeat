package com.nyamnyam.coach.boss.dto.response;

import java.util.List;

public record QuestGenerateResponse(
        Long battleId,
        Long guildId,
        Integer createdCount,
        Integer skippedCount,
        List<GeneratedQuestResponse> quests
) {
}
