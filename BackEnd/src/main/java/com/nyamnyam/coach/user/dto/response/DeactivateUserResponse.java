package com.nyamnyam.coach.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "User deactivation response")
public record DeactivateUserResponse(
        @Schema(description = "User id", example = "1")
        Long userId,

        @Schema(description = "User status", example = "INACTIVE")
        String status,

        @Schema(description = "Deactivated at", example = "2026-05-26T11:30:00")
        LocalDateTime deactivatedAt
) {
}
