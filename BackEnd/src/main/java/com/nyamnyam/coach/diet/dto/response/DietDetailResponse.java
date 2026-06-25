package com.nyamnyam.coach.diet.dto.response;

import com.nyamnyam.coach.diet.entity.MealType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "식단 기록 상세 응답")
public record DietDetailResponse(
        Long dietId,
        MealType mealType,
        LocalDateTime eatenAt,
        String memo,
        BigDecimal totalCalories,
        BigDecimal totalProtein,
        BigDecimal totalCarbs,
        BigDecimal totalFat,
        BigDecimal totalSugar,
        BigDecimal totalSodium,
        List<DietItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
