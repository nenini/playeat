package com.nyamnyam.coach.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Profile image response")
public record ProfileImageResponse(
        @Schema(description = "User id", example = "1")
        Long userId,

        @Schema(description = "Stored profile image path", example = "/uploads/profile-images/profile.png")
        String profileImageUrl
) {
}
