package com.nyamnyam.coach.guild.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Guild notice update request")
public record GuildNoticeUpdateRequest(
        @Schema(description = "Notice title", example = "이번 주 보스전 안내")
        @NotBlank(message = "Notice title is required.")
        @Size(max = 200, message = "Notice title must be 200 characters or less.")
        String title,

        @Schema(description = "Notice content", example = "이번 주는 당류를 줄이는 퀘스트가 많습니다.")
        @NotBlank(message = "Notice content is required.")
        @Size(max = 5000, message = "Notice content must be 5000 characters or less.")
        String content
) {
}
