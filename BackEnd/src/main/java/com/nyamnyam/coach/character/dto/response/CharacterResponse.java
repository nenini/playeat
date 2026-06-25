package com.nyamnyam.coach.character.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "My character response")
public record CharacterResponse(
        Long characterId,
        Long userId,
        String name,
        Integer level,
        Integer xp,
        Integer requiredXp,
        Double xpProgressRate,
        String stage,
        String mood,
        String moodMessage,
        String appearanceType,
        Integer streakDays,
        Integer bestStreakDays,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
