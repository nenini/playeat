package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "My guild list response")
public record MyGuildListResponse(
        List<MyGuildResponse> guilds
) {
}
