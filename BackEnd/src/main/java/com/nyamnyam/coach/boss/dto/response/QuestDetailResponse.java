package com.nyamnyam.coach.boss.dto.response;

import java.time.LocalDateTime;

public record QuestDetailResponse(
        Long questId,
        Long battleId,
        Long guildId,
        Long userId,
        String nickname,
        String profileImageUrl,
        Long characterId,
        String characterName,
        Integer characterLevel,
        String title,
        String description,
        String questType,
        Integer targetValue,
        Integer currentValue,
        String unit,
        Integer damage,
        Integer rewardExp,
        Integer rewardCoin,
        String status,
        String sourceType,
        Boolean isMe,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        LocalDateTime rewardedAt
) {
}
