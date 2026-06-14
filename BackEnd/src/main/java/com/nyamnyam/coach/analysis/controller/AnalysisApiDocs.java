package com.nyamnyam.coach.analysis.controller;

import com.nyamnyam.coach.analysis.dto.response.AnalysisDailyResponse;
import com.nyamnyam.coach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

@Tag(name = "Analysis", description = "분석 화면 조립 API")
public interface AnalysisApiDocs {

    @Operation(summary = "일별 분석 화면 조회", description = "분석 화면에 필요한 식단, 영양 분석, AI 리포트, 코치 피드백을 조합해 조회합니다.")
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<AnalysisDailyResponse>> getDailyAnalysis(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "조회 날짜", example = "2026-06-14") LocalDate date
    );
}
