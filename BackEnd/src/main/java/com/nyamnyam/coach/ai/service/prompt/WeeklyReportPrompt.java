package com.nyamnyam.coach.ai.service.prompt;

import java.time.LocalDate;

public record WeeklyReportPrompt(
        LocalDate startDate,
        LocalDate endDate
) {
}
