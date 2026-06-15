package com.nyamnyam.coach.boss.dto.response;

import java.time.LocalDateTime;

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
        String participantStatus,
        LocalDateTime leftAt,
        Boolean isMe
) {
}
