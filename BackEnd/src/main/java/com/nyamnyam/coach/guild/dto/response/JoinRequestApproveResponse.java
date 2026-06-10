package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Join request approve response")
public record JoinRequestApproveResponse(
        Long requestId,
        Long guildId,
        Long userId,
        String nickname,
        String status,
        Long handledBy,
        LocalDateTime handledAt,
        Long memberId
) {
}
