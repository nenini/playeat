package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Guild member list response")
public record GuildMemberListResponse(
        List<GuildMemberResponse> members
) {
}
