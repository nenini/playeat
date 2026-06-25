package com.nyamnyam.coach.ai.service;

import java.util.List;

public record WeeklyReportContext(
        int averageHealthScore,
        String healthProfileSummary,
        List<String> dailyMealSummaries,
        List<String> dailyNutritionSummaries,
        List<String> repeatedPatterns,
        String retrievalQuery
) {
}
