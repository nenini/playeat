package com.nyamnyam.coach.dashboard.dto.response;

import java.util.List;

public record GuildDashboardResponse(
        Long guildId,
        String guildName,
        Integer myRank,
        int weeklyScore,
        double recordRate,
        int bossDamage,
        int questCompletedCount,
        int questTotalCount,
        List<DailyScoreResponse> dailyScores
) {
}
