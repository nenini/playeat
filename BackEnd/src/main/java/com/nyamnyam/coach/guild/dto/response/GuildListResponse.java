package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Guild list response")
public record GuildListResponse(
        List<GuildSummaryResponse> guilds,
        Integer page,
        Integer size,
        Boolean hasNext
) {
}
