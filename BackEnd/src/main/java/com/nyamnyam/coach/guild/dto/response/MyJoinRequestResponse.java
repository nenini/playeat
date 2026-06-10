package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "My join request response")
public record MyJoinRequestResponse(
        Long requestId,
        Long guildId,
        String guildName,
        String inviteCode,
        String guildDescription,
        String status,
        LocalDateTime createdAt,
        LocalDateTime handledAt,
        String handledByNickname
) {
}
