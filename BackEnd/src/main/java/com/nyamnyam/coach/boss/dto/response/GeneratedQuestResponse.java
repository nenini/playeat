package com.nyamnyam.coach.boss.dto.response;

import java.math.BigDecimal;

public record GeneratedQuestResponse(
        Long questId,
        Long userId,
        String nickname,
        String title,
        String questType,
        Integer targetValue,
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
        String thresholdUnit
) {
}
