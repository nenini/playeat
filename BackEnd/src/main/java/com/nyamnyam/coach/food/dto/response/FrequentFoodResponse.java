package com.nyamnyam.coach.food.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "자주 먹은 음식 응답")
public record FrequentFoodResponse(
        Long foodId,
        String name,
        Long recordCount,
        LocalDateTime lastRecordedAt
) {
}
