package com.nyamnyam.coach.boss.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record BossBattleDetailResponse(
        Long battleId,
        Long guildId,
        String guildName,
        Long bossId,
        String bossName,
        String difficulty,
        String bossImageUrl,
        String status,
        Integer maxHp,
        Integer currentHp,
        Integer totalDamage,
        Double hpRate,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        LocalDateTime endsAt,
        List<BossBattleConditionResponse> commonConditions,
        List<BossBattleDamageLogResponse> recentDamageLogs,
        Integer participantCount,
        Integer activeParticipantCount,
        Integer leftParticipantCount
) {
}
