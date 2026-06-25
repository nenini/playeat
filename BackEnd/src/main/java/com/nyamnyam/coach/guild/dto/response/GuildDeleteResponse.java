package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Guild delete response")
public record GuildDeleteResponse(
        Long guildId,
        String status,
        LocalDateTime deletedAt
) {
}
