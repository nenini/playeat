package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Guild chat list response")
public record GuildChatListResponse(
        Long guildId,
        List<GuildChatResponse> chats,
        Integer page,
        Integer size,
        Boolean hasNext
) {
}
