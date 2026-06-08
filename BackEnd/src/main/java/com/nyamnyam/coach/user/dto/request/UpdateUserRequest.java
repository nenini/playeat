package com.nyamnyam.coach.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User profile update request")
public record UpdateUserRequest(
        @Schema(description = "Nickname", example = "nyamnyam")
        @NotBlank(message = "Nickname is required.")
        @Size(max = 50, message = "Nickname must be 50 characters or less.")
        String nickname
) {
}
