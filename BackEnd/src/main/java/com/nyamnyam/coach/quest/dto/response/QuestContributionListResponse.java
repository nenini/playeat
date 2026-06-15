package com.nyamnyam.coach.quest.dto.response;

import java.util.List;

public record QuestContributionListResponse(
        Long battleId,
        Long guildId,
        List<QuestContributionResponse> contributions
) {
}
