package com.nyamnyam.coach.ranking.dto.response;

import java.time.LocalDate;
import java.util.List;

public record GuildRankingResponse(
        LocalDate weekStartDate,
        LocalDate weekEndDate,
        Integer myGuildRank,
        List<GuildRankingItemResponse> rankings
) {
}
