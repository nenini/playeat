package com.nyamnyam.coach.boss.dto.response;

public record QuestContributionResponse(
        Long userId,
        String nickname,
        String profileImageUrl,
        String characterName,
        Integer characterLevel,
        Integer totalQuestCount,
        Integer completedQuestCount,
        Integer totalDamage,
        Integer expectedDamage,
        Boolean isMe
) {
}
