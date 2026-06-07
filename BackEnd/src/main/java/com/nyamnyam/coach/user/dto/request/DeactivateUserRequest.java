package com.nyamnyam.coach.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User deactivation request")
public record DeactivateUserRequest(
        @Schema(description = "Current password", example = "password123!")
        @NotBlank(message = "Password is required.")
        String password
) {
}
