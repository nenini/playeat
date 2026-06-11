package com.nyamnyam.coach.character.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Character name update request")
public record UpdateCharacterNameRequest(
        @Schema(description = "Character name", example = "냠냠왕")
        @NotBlank(message = "Character name is required.")
        @Size(min = 2, max = 20, message = "Character name must be between 2 and 20 characters.")
        String name
) {
}
