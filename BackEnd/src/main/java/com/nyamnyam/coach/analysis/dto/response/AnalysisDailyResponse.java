package com.nyamnyam.coach.analysis.dto.response;

import com.nyamnyam.coach.ai.dto.response.AiReportResponse;
import com.nyamnyam.coach.coach.dto.response.CoachFeedbackResponse;
import com.nyamnyam.coach.diet.dto.response.DietDayResponse;
import com.nyamnyam.coach.nutrition.dto.response.DailyNutritionAnalysisResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "분석 화면 일별 조립 응답")
public record AnalysisDailyResponse(
        LocalDate date,
        DietDayResponse diet,
        DailyNutritionAnalysisResponse nutrition,
        AiReportResponse dailyReport,
        CoachFeedbackResponse latestMealFeedback
) {
}
