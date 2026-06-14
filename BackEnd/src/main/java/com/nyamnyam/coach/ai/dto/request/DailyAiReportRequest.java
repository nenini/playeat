package com.nyamnyam.coach.ai.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DailyAiReportRequest(
        @NotNull LocalDate date
) {
}
