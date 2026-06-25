package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Guild join request list response")
public record GuildJoinRequestListResponse(
        List<GuildJoinRequestResponse> requests,
        Integer page,
        Integer size,
        Boolean hasNext
) {
}
