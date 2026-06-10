package com.nyamnyam.coach.guild.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Join request create request")
public record JoinRequestCreateRequest(
        @Schema(description = "Join request message", example = "함께 참여하고 싶어요!")
        @Size(max = 500, message = "Message must be 500 characters or less.")
        String message
) {
}
