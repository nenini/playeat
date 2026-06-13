package com.nyamnyam.coach.boss.dto.response;

import java.time.LocalDateTime;

public record BossBattleCreateResponse(
        Long battleId,
        Long guildId,
        Long bossId,
        Long seasonId,
        String bossName,
        String difficulty,
        String status,
        Integer maxHp,
        Integer currentHp,
        LocalDateTime startedAt
) {
}
