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

@Tag(name = "Nutrition", description = "영양 분석 API")
public interface NutritionApiDocs {

    @Operation(summary = "일별 영양 분석 조회", description = "날짜별 식단 기록을 합산해 영양 분석 결과를 조회합니다.")
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<DailyNutritionAnalysisResponse>> getDailyAnalysis(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "조회 날짜", example = "2026-06-14") LocalDate date
    );
}
