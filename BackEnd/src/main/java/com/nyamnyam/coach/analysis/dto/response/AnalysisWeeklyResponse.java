package com.nyamnyam.coach.analysis.dto.response;

import com.nyamnyam.coach.ai.dto.response.AiReportResponse;

import java.time.LocalDate;
import java.util.List;

public record AnalysisWeeklyResponse(
        LocalDate startDate,
        LocalDate endDate,
        int averageHealthScore,
        int scoreDiffFromPreviousWeek,
        int recordRate,
        int recordedDays,
        int totalDays,
        List<AnalysisDailyScoreResponse> dailyScores,
        WeeklyNutritionAverageResponse weeklyNutritionAverage,
        AiReportResponse weeklyReport
) {
}
