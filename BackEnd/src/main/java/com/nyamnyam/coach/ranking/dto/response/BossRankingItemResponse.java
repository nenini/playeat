package com.nyamnyam.coach.ranking.dto.response;

import java.time.LocalDateTime;

public record BossRankingItemResponse(
        int rank,
        Long guildId,
        String guildName,
        boolean myGuild,
        String status,
        int maxHp,
        int currentHp,
        int totalDamage,
        double hpRate,
        LocalDateTime startedAt,
        LocalDateTime endedAt
) {
}
