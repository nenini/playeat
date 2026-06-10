package com.nyamnyam.coach.boss.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Current season boss response")
public record CurrentBossResponse(
        Long seasonId,
        String seasonName,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        List<BossSummaryResponse> bosses,
        List<BossCommonConditionResponse> commonConditions
) {
}
