package com.nyamnyam.coach.boss.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Boss common condition response")
public record BossCommonConditionResponse(
        Long conditionId,
        String title,
        String description,
        String targetType,
        Integer targetValue,
        String unit,
        Integer sortOrder
) {
}
