package com.nyamnyam.coach.dashboard.dto.response;

public record BossBattleDashboardResponse(
        Long battleId,
        Long guildId,
        String bossName,
        String difficulty,
        String status,
        int maxHp,
        int currentHp,
        int totalDamage,
        double hpRate,
        int questCompletedCount,
        int questTotalCount,
        int commonConditionCompletedCount,
        int commonConditionTotalCount,
        int weeklyScore
) {
}
