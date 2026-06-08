package com.nyamnyam.coach.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "회원가입 응답")
public record SignupResponse(
        @Schema(description = "생성된 사용자 ID", example = "1")
        Long userId,

        @Schema(description = "가입 이메일", example = "user@example.com")
        String email,

        @Schema(description = "가입 닉네임", example = "냠냠러")
        String nickname,

        @Schema(description = "온보딩 완료 여부", example = "false")
        Boolean onboardingCompleted,

        @Schema(description = "가입 시각", example = "2026-05-26T10:00:00")
        LocalDateTime createdAt
) {
}
