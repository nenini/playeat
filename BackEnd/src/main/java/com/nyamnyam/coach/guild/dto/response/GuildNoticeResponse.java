package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Guild notice response")
public record GuildNoticeResponse(
        Long noticeId,
        Long guildId,
        Long writerUserId,
        String writerNickname,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean editable
) {
}
