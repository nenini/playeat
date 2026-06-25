package com.nyamnyam.coach.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Onboarding response")
public record OnboardingResponse(
        @Schema(description = "User id", example = "1")
        Long userId,

        @Schema(description = "Onboarding completed", example = "true")
        Boolean onboardingCompleted,

        @Schema(description = "Selected coach id", example = "1")
        Long selectedCoachId,

        @Schema(description = "Health profile id", example = "1")
        Long healthProfileId
) {
}
