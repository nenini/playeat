package com.nyamnyam.coach.ai.service.parser;

import java.util.List;

public record DailyReportContent(
        String summary,
        List<String> strengths,
        List<String> warnings,
        String nextAction
) {
}
