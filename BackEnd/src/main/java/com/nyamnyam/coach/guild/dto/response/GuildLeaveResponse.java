package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Guild leave response")
public record GuildLeaveResponse(
        Long guildId,
        Long userId,
        LocalDateTime leftAt
) {
}
