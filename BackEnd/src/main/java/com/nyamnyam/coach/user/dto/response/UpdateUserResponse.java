package com.nyamnyam.coach.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "User profile update response")
public record UpdateUserResponse(
        @Schema(description = "User id", example = "1")
        Long userId,

        @Schema(description = "Nickname", example = "nyamnyam")
        String nickname,

        @Schema(description = "Updated at", example = "2026-05-26T11:00:00")
        LocalDateTime updatedAt
) {
}
