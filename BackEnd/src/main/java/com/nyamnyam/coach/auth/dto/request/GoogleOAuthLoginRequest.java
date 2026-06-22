package com.nyamnyam.coach.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleOAuthLoginRequest(
        @NotBlank(message = "Google 인증 코드는 필수입니다.")
        String code,

        @NotBlank(message = "OAuth redirect URI는 필수입니다.")
        String redirectUri
) {
}
