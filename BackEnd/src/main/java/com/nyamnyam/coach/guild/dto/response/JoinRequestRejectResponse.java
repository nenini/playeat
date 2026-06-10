package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Join request reject response")
public record JoinRequestRejectResponse(
        Long requestId,
        Long guildId,
        Long userId,
        String status,
        Long handledBy,
        LocalDateTime handledAt
) {
}
