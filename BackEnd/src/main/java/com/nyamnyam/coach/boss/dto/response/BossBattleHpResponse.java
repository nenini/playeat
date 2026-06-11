package com.nyamnyam.coach.boss.dto.response;

public record BossBattleHpResponse(
        Long battleId,
        String status,
        Integer maxHp,
        Integer currentHp,
        Integer totalDamage,
        Double hpRate
) {
}
