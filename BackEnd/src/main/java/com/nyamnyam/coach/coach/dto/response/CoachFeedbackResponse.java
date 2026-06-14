package com.nyamnyam.coach.coach.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "코치 끼니 피드백 응답")
public record CoachFeedbackResponse(
        Long feedbackId,
        Long dietId,
        Long coachId,
        String coachName,
        String message,
        String modelName,
        LocalDateTime createdAt
) {
}
