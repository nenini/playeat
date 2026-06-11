package com.nyamnyam.coach.character.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Character name update response")
public record UpdateCharacterNameResponse(
        Long characterId,
        String name,
        LocalDateTime updatedAt
) {
}
