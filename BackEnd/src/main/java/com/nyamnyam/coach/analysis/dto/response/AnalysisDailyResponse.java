package com.nyamnyam.coach.analysis.dto.response;

import com.nyamnyam.coach.ai.dto.response.AiReportResponse;
import com.nyamnyam.coach.coach.dto.response.CoachFeedbackResponse;
import com.nyamnyam.coach.diet.dto.response.DietDayResponse;
import com.nyamnyam.coach.nutrition.dto.response.DailyNutritionAnalysisResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Analysis daily response")
public record AnalysisDailyResponse(
        LocalDate date,
        int healthScore,
        DietDayResponse diet,
        DailyNutritionAnalysisResponse nutrition,
        AiReportResponse dailyReport,
        List<CoachFeedbackResponse> mealFeedbacks,
        CoachFeedbackResponse latestMealFeedback
) {
}
