package com.nyamnyam.coach.nutrition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Nutrition standard insight response")
public record StandardInsightResponse(
        String nutrient,
        String label,
        String unit,
        BigDecimal current,
        BigDecimal standard,
        String status
) {
}
