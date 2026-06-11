package com.nyamnyam.coach.boss.dto.response;

public record BossBattleConditionResponse(
        Long battleConditionId,
        String title,
        String description,
        String targetType,
        Integer targetValue,
        Integer currentValue,
        String unit,
        Boolean completed,
        Integer sortOrder
) {
}
