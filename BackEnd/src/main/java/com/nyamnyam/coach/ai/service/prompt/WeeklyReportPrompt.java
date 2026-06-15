package com.nyamnyam.coach.ai.service.prompt;

import com.nyamnyam.coach.ai.rag.document.RagReference;

import java.time.LocalDate;
import java.util.List;

public record WeeklyReportPrompt(
        LocalDate startDate,
        LocalDate endDate,
        int averageHealthScore,
        String healthProfileSummary,
        List<String> dailyMealSummaries,
        List<String> dailyNutritionSummaries,
        List<String> repeatedPatterns,
        List<RagReference> ragReferences
) {
}
