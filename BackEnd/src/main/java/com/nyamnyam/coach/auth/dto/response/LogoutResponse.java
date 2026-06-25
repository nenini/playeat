package com.nyamnyam.coach.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "로그아웃 응답")
public record LogoutResponse(
        @Schema(description = "로그아웃 처리 시각", example = "2026-05-26T10:30:00")
        LocalDateTime loggedOutAt
) {
}
