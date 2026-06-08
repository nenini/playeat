package com.nyamnyam.coach.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 재발급 응답")
public record TokenRefreshResponse(
        @Schema(description = "새 access token", example = "new-jwt-access-token")
        String accessToken,

        @Schema(description = "새 refresh token", example = "new-jwt-refresh-token")
        String refreshToken,

        @Schema(description = "토큰 인증 방식", example = "Bearer")
        String tokenType,

        @Schema(description = "access token 만료 시간, 초 단위", example = "3600")
        long expiresIn
) {
}
