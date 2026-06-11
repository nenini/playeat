package com.nyamnyam.coach.boss.dto.response;

import java.util.List;

public record QuestContributionListResponse(
        Long battleId,
        Long guildId,
        List<QuestContributionResponse> contributions
) {
}
