package com.nyamnyam.coach.boss.dto.response;

import java.time.LocalDateTime;

public record RewardClaimResponse(
        String sourceType,
        Long sourceId,
        int xpAmount,
        int coinAmount,
        LocalDateTime claimedAt
) {
}
