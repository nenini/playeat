package com.nyamnyam.coach.quest.dto.response;

public record QuestVerifyResponse(
        Long questId,
        Long battleId,
        Long guildId,
        String status,
        boolean verified,
        int damage,
        int currentHp,
        int totalDamage,
        String bossBattleStatus,
        String message
) {
}
