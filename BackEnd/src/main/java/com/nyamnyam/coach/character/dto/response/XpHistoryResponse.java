package com.nyamnyam.coach.character.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Character XP history item")
public record XpHistoryResponse(
        Long xpHistoryId,
        String sourceType,
        Long sourceId,
        Integer xpAmount,
        String reason,
        LocalDateTime createdAt
) {
}
