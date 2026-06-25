package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Guild update response")
public record GuildUpdateResponse(
        Long guildId,
        String name,
        String description,
        String inviteCode,
        Integer memberCount,
        Integer maxMembers,
        String visibility,
        String status,
        LocalDateTime updatedAt
) {
}
