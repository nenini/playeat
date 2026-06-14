package com.nyamnyam.coach.ranking.dto.response;

import java.util.List;

public record BossRankingResponse(
        Long bossId,
        String bossName,
        String difficulty,
        Integer myGuildRank,
        List<BossRankingItemResponse> rankings
) {
}
