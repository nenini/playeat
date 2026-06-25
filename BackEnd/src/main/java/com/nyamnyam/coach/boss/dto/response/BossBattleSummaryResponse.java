package com.nyamnyam.coach.boss.dto.response;

import java.time.LocalDateTime;

public record BossBattleSummaryResponse(
        Long battleId,
        Long guildId,
        Long bossId,
        String bossName,
        String difficulty,
        String bossImageUrl,
        String status,
        Integer maxHp,
        Integer currentHp,
        Integer totalDamage,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        LocalDateTime endsAt,
        Boolean rewardClaimed
) {
}
