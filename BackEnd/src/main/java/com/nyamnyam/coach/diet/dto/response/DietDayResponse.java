package com.nyamnyam.coach.diet.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "날짜별 식단 보드 응답")
public record DietDayResponse(
        LocalDate date,
        List<DietMealResponse> meals,
        DailyNutritionSummaryResponse dailySummary
) {
}
