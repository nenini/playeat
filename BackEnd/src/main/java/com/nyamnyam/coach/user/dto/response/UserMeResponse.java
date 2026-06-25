package com.nyamnyam.coach.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Current user response")
public record UserMeResponse(
        @Schema(description = "User id", example = "1")
        Long userId,

        @Schema(description = "Email", example = "user@example.com")
        String email,

        @Schema(description = "Nickname", example = "nyamnyam")
        String nickname,

        @Schema(description = "Stored profile image path", example = "/uploads/profile-images/profile.png")
        String profileImageUrl,

        @Schema(description = "Selected coach id", example = "1")
        Long selectedCoachId,

        @Schema(description = "User status", example = "ACTIVE")
        String status,

        @Schema(description = "Login provider", example = "GOOGLE")
        String provider,

        @Schema(description = "Whether local password exists", example = "false")
        Boolean hasPassword,

        @Schema(description = "Onboarding completed", example = "false")
        Boolean onboardingCompleted,

        @Schema(description = "Created at", example = "2026-05-26T10:00:00")
        LocalDateTime createdAt
) {
}
