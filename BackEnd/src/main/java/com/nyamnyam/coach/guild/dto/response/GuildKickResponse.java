package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Guild kick response")
public record GuildKickResponse(
        Long guildId,
        Long memberId,
        Long userId,
        LocalDateTime kickedAt
) {
}
