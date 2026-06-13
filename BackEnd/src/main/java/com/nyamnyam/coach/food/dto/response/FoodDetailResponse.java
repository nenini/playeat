package com.nyamnyam.coach.food.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "음식 상세 응답")
public record FoodDetailResponse(
        Long foodId,
        String externalFoodCode,
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
        BigDecimal sodium,
        BigDecimal fiber,
        BigDecimal iron,
        BigDecimal phosphorus,
        BigDecimal potassium,
        BigDecimal vitaminAUgRae,
        BigDecimal betaCaroteneUg,
        BigDecimal retinolUg,
        String source
) {
}
