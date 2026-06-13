package com.nyamnyam.coach.diet.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "식단 음식 항목 응답")
public record DietItemResponse(
        Long dietItemId,
        Long foodId,
        String foodName,
        String brand,
        BigDecimal inputAmount,
        String inputUnit,
        BigDecimal amountG,
        BigDecimal amountMl,
        BigDecimal calories,
        BigDecimal protein,
        BigDecimal carbs,
        BigDecimal fat,
        BigDecimal sugar,
        BigDecimal sodium
) {
}
