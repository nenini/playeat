package com.nyamnyam.coach.quest.dto.response;

import java.math.BigDecimal;
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
        Long questTemplateId,
        String conditionCategory,
        String metricType,
        String comparisonType,
        String aggregationType,
        String evaluationScope,
        BigDecimal thresholdValue,
        BigDecimal thresholdMinValue,
        BigDecimal thresholdMaxValue,
        String thresholdUnit,
        Boolean isMe,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        LocalDateTime rewardedAt
) {
}
