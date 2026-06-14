package com.nyamnyam.coach.ai.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record WeeklyAiReportRequest(
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {
}
