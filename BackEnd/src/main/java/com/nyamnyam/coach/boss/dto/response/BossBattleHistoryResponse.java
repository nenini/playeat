package com.nyamnyam.coach.boss.dto.response;

import java.util.List;

public record BossBattleHistoryResponse(
        List<BossBattleSummaryResponse> battles,
        Integer page,
        Integer size,
        Boolean hasNext
) {
}
