package com.nyamnyam.coach.boss.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Boss detail response")
public record BossDetailResponse(
        Long bossId,
        Long seasonId,
        String name,
        String description,
        String difficulty,
        Integer maxHp,
        String imageUrl,
        Integer rewardExp,
        Integer rewardCoin,
        String status,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        List<BossCommonConditionResponse> commonConditions
) {
}
