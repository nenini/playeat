package com.nyamnyam.coach.food.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "음식 검색 항목 응답")
public record FoodSearchResponse(
        Long foodId,
        String name,
        String brand,
        String category,
        BigDecimal nutritionBasisAmount,
        String nutritionBasisUnit,
        BigDecimal servingAmount,
        String servingUnit,
        BigDecimal gramPerPiece,
        BigDecimal calories,
        BigDecimal protein,
        BigDecimal carbs,
        BigDecimal fat,
        BigDecimal sugar,
        BigDecimal sodium
) {
}
