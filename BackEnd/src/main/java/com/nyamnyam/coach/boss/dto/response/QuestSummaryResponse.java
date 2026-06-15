package com.nyamnyam.coach.boss.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record QuestSummaryResponse(
        Long questId,
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
        String participantStatus,
        LocalDateTime leftAt
) {
}
