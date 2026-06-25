package com.nyamnyam.coach.food.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "자주 먹은 음식 목록 응답")
public record FrequentFoodListResponse(
        List<FrequentFoodResponse> foods
) {
}
