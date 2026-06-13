package com.nyamnyam.coach.diet.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "식단 기록 삭제 응답")
public record DietDeleteResponse(
        Long dietId,
        boolean deleted,
        LocalDateTime deletedAt
) {
}
