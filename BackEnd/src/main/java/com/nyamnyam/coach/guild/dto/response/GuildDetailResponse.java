package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Guild detail response")
public record GuildDetailResponse(
        Long guildId,
        String name,
        String description,
        String inviteCode,
        Long ownerUserId,
        String ownerNickname,
        Integer memberCount,
        Integer maxMembers,
        Integer guildPoint,
        String visibility,
        String status,
        String myRole,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
