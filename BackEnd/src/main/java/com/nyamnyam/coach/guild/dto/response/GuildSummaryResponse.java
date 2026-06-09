package com.nyamnyam.coach.guild.dto.response;

import com.nyamnyam.coach.guild.entity.MyGuildJoinStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Guild summary response")
public record GuildSummaryResponse(
        Long guildId,
        String name,
        String description,
        String inviteCode,
        Integer memberCount,
        Integer maxMembers,
        Integer guildPoint,
        String ownerNickname,
        MyGuildJoinStatus myJoinStatus,
        Long joinRequestId,
        Boolean alreadyJoinedAnyGuild
) {
}
