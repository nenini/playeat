package com.nyamnyam.coach.ranking.dto.response;

public record GuildRankingItemResponse(
        int rank,
        Long guildId,
        String guildName,
        boolean myGuild,
        int weeklyScore,
        double recordRate,
        double questCompletionRate,
        int bossDamage
) {
}
