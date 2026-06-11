package com.nyamnyam.coach.boss.dto.response;

import java.time.LocalDateTime;

public record BossBattleDamageLogResponse(
        Long damageLogId,
        Long userId,
        String nickname,
        Integer damage,
        String sourceType,
        String description,
        LocalDateTime createdAt
) {
}
