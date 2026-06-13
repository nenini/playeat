package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Guild chat response")
public record GuildChatResponse(
        Long chatId,
        Long guildId,
        Long userId,
        String nickname,
        String profileImageUrl,
        Long characterId,
        String characterName,
        Integer characterLevel,
        String messageType,
        String message,
        LocalDateTime createdAt,
        Boolean isMe
) {
}
