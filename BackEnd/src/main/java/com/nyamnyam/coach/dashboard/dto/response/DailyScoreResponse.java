package com.nyamnyam.coach.dashboard.dto.response;

public record DailyScoreResponse(
        String dayOfWeek,
        int score
) {
}
