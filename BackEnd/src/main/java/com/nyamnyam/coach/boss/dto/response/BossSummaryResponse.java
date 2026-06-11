package com.nyamnyam.coach.boss.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Boss summary response")
public record BossSummaryResponse(
        Long bossId,
        String name,
        String description,
        String difficulty,
        Integer maxHp,
        String imageUrl,
        Integer rewardExp,
        Integer rewardCoin,
        List<BossCommonConditionResponse> commonConditions
) {
}
