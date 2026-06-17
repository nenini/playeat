package com.nyamnyam.coach.nutrition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "또래 비교 항목")
public record PeerComparisonResponse(
        @Schema(description = "영양소 코드", example = "protein")
        String code,
        @Schema(description = "영양소 이름", example = "단백질")
        String label,
        @Schema(description = "단위", example = "g")
        String unit,
        @Schema(description = "내 섭취량", example = "60.00")
        BigDecimal userValue,
        @Schema(description = "또래 평균 섭취량", example = "74.00")
        BigDecimal peerAverage,
        @Schema(description = "상태 (LOW/HIGH/OK)", example = "LOW")
        String status,
        @Schema(description = "메시지", example = "또래 평균보다 14g 부족해요")
        String message
) {
}
