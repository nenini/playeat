package com.nyamnyam.coach.boss.dto.response;

import java.util.List;

public record CommonConditionVerifyResponse(
        Long battleId,
        String bossBattleStatus,
        Integer currentHp,
        Integer totalDamage,
        List<CommonConditionVerifyItemResponse> conditions
) {
}
