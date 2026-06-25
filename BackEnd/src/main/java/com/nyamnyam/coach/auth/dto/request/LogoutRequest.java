package com.nyamnyam.coach.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그아웃 요청")
public record LogoutRequest(
        @Schema(description = "폐기할 refresh token", example = "jwt-refresh-token")
        @NotBlank(message = "refresh token은 필수입니다.")
        String refreshToken
) {
}
