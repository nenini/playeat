package com.nyamnyam.coach.diet.dto.response;

import com.nyamnyam.coach.diet.entity.MealType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "날짜별 끼니 카드 응답")
public record DietMealResponse(
        MealType mealType,
        String label,
        String timeRange,
        boolean recorded,
        Long dietId,
        LocalDateTime eatenAt,
        String memo,
        BigDecimal totalCalories,
        BigDecimal totalProtein,
        BigDecimal totalCarbs,
        BigDecimal totalFat,
        List<DietItemResponse> items
) {
}
