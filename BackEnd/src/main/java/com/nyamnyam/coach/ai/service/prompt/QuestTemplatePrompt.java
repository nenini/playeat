package com.nyamnyam.coach.ai.service.prompt;

import java.math.BigDecimal;

public record QuestTemplatePrompt(
        Long templateId,
        String title,
        String description,
        String questType,
        String conditionCategory,
        String metricType,
        String comparisonType,
        String aggregationType,
        String evaluationScope,
        BigDecimal thresholdValue,
        BigDecimal thresholdMinValue,
        BigDecimal thresholdMaxValue,
        String thresholdUnit,
        Integer targetValue,
        String unit
) {
}
