package com.nyamnyam.coach.ai.service.prompt;

import com.nyamnyam.coach.nutrition.dto.response.NutrientAnalysisResponse;

import java.time.LocalDate;
import java.util.List;

public record DailyReportPrompt(
        LocalDate date,
        int healthScore,
        List<NutrientAnalysisResponse> nutrients,
        List<String> mealSummaries
) {
}
