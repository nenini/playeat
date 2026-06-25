package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Join request cancel response")
public record JoinRequestCancelResponse(
        Long requestId,
        Long guildId,
        String status,
        LocalDateTime canceledAt
) {
}
