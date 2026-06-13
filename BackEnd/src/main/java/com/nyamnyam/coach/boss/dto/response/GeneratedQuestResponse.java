package com.nyamnyam.coach.boss.dto.response;

public record GeneratedQuestResponse(
        Long questId,
        Long userId,
        String nickname,
        String title,
        String questType,
        Integer targetValue,
        String unit,
        Integer damage,
        Integer rewardExp,
        Integer rewardCoin,
        String status
) {
}
