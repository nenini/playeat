package com.nyamnyam.coach.ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "AI 리포트 응답")
public record AiReportResponse(
        Long reportId,
        String reportType,
        LocalDate periodStart,
        LocalDate periodEnd,
        int healthScore,
        String summary,
        List<String> strengths,
        List<String> warnings,
        String nextAction,
        LocalDateTime createdAt
) {
}
