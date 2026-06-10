package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Guild create response")
public record GuildCreateResponse(
        Long guildId,
        String name,
        String description,
        String inviteCode,
        Long ownerUserId,
        String myRole,
        Integer memberCount,
        Integer maxMembers,
        String visibility,
        String status,
        LocalDateTime createdAt
) {
}
