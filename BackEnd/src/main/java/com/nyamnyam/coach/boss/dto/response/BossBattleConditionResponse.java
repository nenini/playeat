package com.nyamnyam.coach.boss.dto.response;

import java.math.BigDecimal;

public record BossBattleConditionResponse(
        Long battleConditionId,
        String title,
        String description,
        String targetType,
        BigDecimal thresholdValue,
        String thresholdUnit,
        Integer targetValue,
        Integer requiredDays,
        Integer currentValue,
        Integer damage,
        String unit,
        Boolean completed,
        Integer sortOrder
) {
}
