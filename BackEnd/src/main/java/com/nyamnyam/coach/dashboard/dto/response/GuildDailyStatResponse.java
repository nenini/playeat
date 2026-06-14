package com.nyamnyam.coach.dashboard.dto.response;

import java.time.LocalDate;

public record GuildDailyStatResponse(
        LocalDate date,
        String dayOfWeek,
        int recordCount,
        int activeMemberCount,
        double recordRate,
        int questCompletedCount,
        int damage,
        int score
) {
}
