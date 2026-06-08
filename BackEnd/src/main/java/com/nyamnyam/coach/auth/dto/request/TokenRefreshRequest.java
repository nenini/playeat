package com.nyamnyam.coach.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 재발급 요청")
public record TokenRefreshRequest(
        @Schema(description = "로그인 또는 이전 재발급에서 받은 refresh token", example = "jwt-refresh-token")
        @NotBlank(message = "refresh token은 필수입니다.")
        String refreshToken
) {
}
