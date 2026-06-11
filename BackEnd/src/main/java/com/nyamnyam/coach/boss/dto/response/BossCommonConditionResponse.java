package com.nyamnyam.coach.boss.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Boss common condition response")
public record BossCommonConditionResponse(
        Long conditionId,
        String title,
        String description,
        String targetType,
        BigDecimal thresholdValue,
        String thresholdUnit,
        Integer targetValue,
        Integer requiredDays,
        String unit,
        Integer sortOrder
) {
}
