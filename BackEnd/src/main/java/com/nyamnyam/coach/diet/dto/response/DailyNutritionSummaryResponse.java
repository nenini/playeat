package com.nyamnyam.coach.diet.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "일일 영양 합계 응답")
public record DailyNutritionSummaryResponse(
        BigDecimal totalCalories,
        BigDecimal targetCalories,
        int calorieRate,
        BigDecimal totalProtein,
        BigDecimal targetProtein,
        int proteinRate,
        BigDecimal totalCarbs,
        BigDecimal targetCarbs,
        int carbsRate,
        BigDecimal totalFat,
        BigDecimal targetFat,
        int fatRate,
        int vegetableServings,
        int targetVegetableServings,
        int vegetableRate
) {
}
