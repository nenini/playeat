package com.nyamnyam.coach.dashboard.dto.response;

import java.time.LocalDate;
import java.util.List;

public record GuildWeeklyReportResponse(
        Long guildId,
        String guildName,
        LocalDate weekStartDate,
        LocalDate weekEndDate,
        double recordRate,
        int bossDamage,
        int weeklyScore,
        int questCompletedCount,
        int questTotalCount,
        List<GuildDailyStatResponse> dailyStats
) {
}
