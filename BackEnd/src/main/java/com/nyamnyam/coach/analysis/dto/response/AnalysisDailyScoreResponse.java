package com.nyamnyam.coach.analysis.dto.response;

import java.time.LocalDate;

public record AnalysisDailyScoreResponse(
        LocalDate date,
        String dayOfWeek,
        int healthScore
) {
}
