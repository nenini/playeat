package com.nyamnyam.coach.nutrition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "일별 영양 분석 응답")
public record DailyNutritionAnalysisResponse(
        LocalDate date,
        int healthScore,
        List<NutrientAnalysisResponse> nutrients,
        List<PeerComparisonResponse> peerComparison
) {
}
