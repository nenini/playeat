package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "My guild response")
public record MyGuildResponse(
        Long guildId,
        String name,
        String description,
        String inviteCode,
        Integer memberCount,
        Integer maxMembers,
        Integer guildPoint,
        String myRole,
        LocalDateTime joinedAt
) {
}
