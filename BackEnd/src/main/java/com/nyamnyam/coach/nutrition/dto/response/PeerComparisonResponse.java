package com.nyamnyam.coach.nutrition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "또래 비교 응답")
public record PeerComparisonResponse(
        String label,
        int userRate,
        int peerAverageRate,
        String message
) {
}
