package com.nyamnyam.coach.nutrition.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.nutrition.dto.response.DailyNutritionAnalysisResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

@Tag(name = "Nutrition", description = "Internal nutrition calculation API")
public interface NutritionApiDocs {

    @Deprecated
    @Operation(
            summary = "일별 영양 분석 조회(deprecated)",
            description = "화면에서는 GET /api/v1/analysis/daily 또는 GET /api/v1/diets를 사용합니다.",
            deprecated = true
    )
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<DailyNutritionAnalysisResponse>> getDailyAnalysis(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "조회 날짜", example = "2026-06-14") LocalDate date
    );
}
