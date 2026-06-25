package com.nyamnyam.coach.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 응답에 포함되는 사용자 요약 정보")
public record AuthUserResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "사용자 이메일", example = "user@example.com")
        String email,

        @Schema(description = "사용자 닉네임", example = "냠냠러")
        String nickname,

        @Schema(description = "온보딩 완료 여부", example = "true")
        Boolean onboardingCompleted
) {
}
