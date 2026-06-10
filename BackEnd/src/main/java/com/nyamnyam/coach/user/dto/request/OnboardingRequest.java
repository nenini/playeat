package com.nyamnyam.coach.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Onboarding request")
public record OnboardingRequest(
        @Schema(description = "Selected coach id", example = "1")
        Long selectedCoachId,

        @Schema(description = "Health profile")
        @Valid
        @NotNull(message = "Health profile is required.")
        HealthProfileRequest healthProfile
) {
}
