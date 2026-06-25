package com.nyamnyam.coach.food.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "음식 검색 페이지 응답")
public record FoodSearchPageResponse(
        List<FoodSearchResponse> foods,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
