package com.nyamnyam.coach.nutrition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Daily nutrition analysis response")
public record DailyNutritionAnalysisResponse(
        LocalDate date,
        int healthScore,
        List<NutrientAnalysisResponse> nutrients,
        List<StandardInsightResponse> standardInsights
) {
}
