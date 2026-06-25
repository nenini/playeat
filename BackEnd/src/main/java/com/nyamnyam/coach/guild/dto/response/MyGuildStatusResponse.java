package com.nyamnyam.coach.guild.dto.response;

import com.nyamnyam.coach.guild.entity.MyGuildJoinStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "My guild status response")
public record MyGuildStatusResponse(
        MyGuildJoinStatus status,
        GuildStatusInfo guild,
        JoinRequestInfo joinRequest
) {

    public record GuildStatusInfo(
            Long guildId,
            String name,
            String inviteCode,
            String role
    ) {
    }

    public record JoinRequestInfo(
            Long requestId,
            String status,
            LocalDateTime createdAt
    ) {
    }
}
