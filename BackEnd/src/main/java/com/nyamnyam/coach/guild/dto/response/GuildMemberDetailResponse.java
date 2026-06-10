package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Guild member detail response")
public record GuildMemberDetailResponse(
        Long memberId,
        Long userId,
        String nickname,
        String profileImageUrl,
        Long characterId,
        String characterName,
        Integer characterLevel,
        String characterStage,
        String characterMood,
        String characterAppearanceType,
        Integer streakDays,
        String role,
        LocalDateTime joinedAt,
        Boolean isMe,
        Integer weeklyRecordRate,
        Integer bossContribution,
        Integer completedQuestCount
) {
}
