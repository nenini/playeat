package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Guild notice delete response")
public record GuildNoticeDeleteResponse(
        Long guildId,
        Long noticeId,
        Boolean deleted
) {
}
