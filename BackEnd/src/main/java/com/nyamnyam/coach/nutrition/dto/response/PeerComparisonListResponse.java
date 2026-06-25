package com.nyamnyam.coach.nutrition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "또래 비교 응답")
public record PeerComparisonListResponse(
        @Schema(description = "데이터 출처", example = "2022 국민건강영양조사 (질병관리청)")
        String sourceName,
        @Schema(description = "또래 비교 항목 목록")
        List<PeerComparisonResponse> items
) {
}
