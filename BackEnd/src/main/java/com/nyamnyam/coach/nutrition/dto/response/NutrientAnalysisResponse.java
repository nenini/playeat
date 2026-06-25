package com.nyamnyam.coach.nutrition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "영양소별 분석 응답")
public record NutrientAnalysisResponse(
        String code,
        String name,
        String unit,
        BigDecimal current,
        BigDecimal target,
        int achievementRate,
        String status
) {
    public NutrientAnalysisResponse(
            String code,
            String name,
            String unit,
            BigDecimal current,
            BigDecimal target,
            int achievementRate,
            String status,
            String ignoredMessage
    ) {
        this(code, name, unit, current, target, achievementRate, status);
    }
}
