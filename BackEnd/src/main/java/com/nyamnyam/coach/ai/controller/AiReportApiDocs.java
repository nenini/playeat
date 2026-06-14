package com.nyamnyam.coach.ai.controller;

import com.nyamnyam.coach.ai.dto.request.DailyAiReportRequest;
import com.nyamnyam.coach.ai.dto.request.WeeklyAiReportRequest;
import com.nyamnyam.coach.ai.dto.response.AiReportResponse;
import com.nyamnyam.coach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

@Tag(name = "AI Report", description = "AI 리포트 API")
public interface AiReportApiDocs {

    @Operation(summary = "일간 AI 리포트 생성", description = "일간 식단 분석 결과를 바탕으로 AI 리포트를 생성합니다.")
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<AiReportResponse>> createDailyReport(
            @Parameter(hidden = true) Authentication authentication,
            DailyAiReportRequest request
    );

    @Operation(summary = "일간 AI 리포트 조회", description = "생성된 일간 AI 리포트를 조회합니다.")
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<AiReportResponse>> getDailyReport(
            @Parameter(hidden = true) Authentication authentication,
            LocalDate date
    );

    @Operation(summary = "주간 AI 리포트 생성", description = "주간 식단 기록을 바탕으로 AI 리포트를 생성합니다.")
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<AiReportResponse>> createWeeklyReport(
            @Parameter(hidden = true) Authentication authentication,
            WeeklyAiReportRequest request
    );

    @Operation(summary = "주간 AI 리포트 조회", description = "생성된 주간 AI 리포트를 조회합니다.")
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<AiReportResponse>> getWeeklyReport(
            @Parameter(hidden = true) Authentication authentication,
            LocalDate startDate,
            LocalDate endDate
    );
}
