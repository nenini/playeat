package com.nyamnyam.coach.guild.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Join request create by invite code request")
public record JoinRequestCreateByCodeRequest(
        @Schema(description = "Guild invite code", example = "NYAM-A7K3")
        @NotBlank(message = "Invite code is required.")
        @Size(max = 50, message = "Invite code must be 50 characters or less.")
        String inviteCode,

        @Schema(description = "Join request message", example = "함께 참여하고 싶어요!")
        @Size(max = 500, message = "Message must be 500 characters or less.")
        String message
) {
}
