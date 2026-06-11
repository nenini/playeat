package com.nyamnyam.coach.boss.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Boss battle create request")
public record BossBattleCreateRequest(
        @Schema(description = "Boss ID", example = "1")
        @NotNull(message = "Boss ID is required.")
        Long bossId
) {
}
