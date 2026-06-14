package com.nyamnyam.coach.ai.service.prompt;

import java.time.LocalDate;

public record DailyReportPrompt(
        LocalDate date,
        int healthScore
) {
}
