package com.nyamnyam.coach.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record LoginResponse(
        @Schema(description = "보호 API 호출에 사용할 access token", example = "jwt-access-token")
        String accessToken,

        @Schema(description = "access token 재발급에 사용할 refresh token", example = "jwt-refresh-token")
        String refreshToken,

        @Schema(description = "토큰 인증 방식", example = "Bearer")
        String tokenType,

        @Schema(description = "access token 만료 시간, 초 단위", example = "3600")
        long expiresIn,

        @Schema(description = "로그인 사용자 요약 정보")
        AuthUserResponse user
) {
}
