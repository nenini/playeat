package com.nyamnyam.coach.guild.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Guild update request")
public record GuildUpdateRequest(
        @Schema(description = "Guild name", example = "단백질 원정대")
        @NotBlank(message = "Guild name is required.")
        @Size(max = 100, message = "Guild name must be 100 characters or less.")
        String name,

        @Schema(description = "Guild description", example = "건강하게 먹고 함께 보스 잡는 길드")
        @Size(max = 500, message = "Guild description must be 500 characters or less.")
        String description,

        @Schema(description = "Maximum members", example = "30")
        @Min(value = 1, message = "Max members must be at least 1.")
        @Max(value = 30, message = "Max members must be 30 or less.")
        Integer maxMembers
) {
}
