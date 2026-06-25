package com.nyamnyam.coach.analysis.controller;

import com.nyamnyam.coach.analysis.dto.response.AnalysisDailyResponse;
import com.nyamnyam.coach.analysis.dto.response.AnalysisWeeklyResponse;
import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.nutrition.dto.response.PeerComparisonListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

@Tag(name = "Analysis", description = "Analysis screen API")
public interface AnalysisApiDocs {

    @Operation(summary = "일별 분석 조회", description = "일별 식단, 영양 목표 달성률, health score, 리포트, 코치 피드백을 조회합니다.")
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<AnalysisDailyResponse>> getDailyAnalysis(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "조회 날짜", example = "2026-06-14") LocalDate date
    );

    @Operation(summary = "주간 분석 조회", description = "주간 평균 점수, 기록률, 일별 건강 점수 추이, 주간 평균 영양소를 조회합니다.")
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<AnalysisWeeklyResponse>> getWeeklyAnalysis(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "조회 시작일(월요일)", example = "2026-05-11") LocalDate startDate,
            @Parameter(description = "조회 종료일(시작일 + 6일)", example = "2026-05-17") LocalDate endDate
    );

    @Operation(
            summary = "또래 비교 조회",
            description = "동성·동나이대 또래의 평균 섭취량과 해당 날짜의 내 섭취량을 비교합니다. 데이터 출처: 질병관리청 국민건강영양조사."
    )
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<PeerComparisonListResponse>> getPeerComparison(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "조회 날짜", example = "2026-06-14") LocalDate date
    );
}
