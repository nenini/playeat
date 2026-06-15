package com.nyamnyam.coach.boss.dto.response;

public record CommonConditionVerifyItemResponse(
        Long battleConditionId,
        String title,
        Integer currentValue,
        Integer targetValue,
        Boolean completed,
        Boolean newlyCompleted,
        Integer damage
) {
}
