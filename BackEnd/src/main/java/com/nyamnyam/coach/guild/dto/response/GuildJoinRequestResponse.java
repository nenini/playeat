package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Guild join request response")
public record GuildJoinRequestResponse(
        Long requestId,
        Long guildId,
        Long userId,
        String nickname,
        String profileImageUrl,
        Long characterId,
        String characterName,
        Integer characterLevel,
        String status,
        LocalDateTime createdAt
) {
}
