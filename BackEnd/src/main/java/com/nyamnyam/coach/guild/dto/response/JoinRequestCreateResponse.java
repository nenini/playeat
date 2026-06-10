package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Join request create response")
public record JoinRequestCreateResponse(
        Long requestId,
        Long guildId,
        String guildName,
        String inviteCode,
        String status,
        String message,
        LocalDateTime createdAt
) {
}
