package com.nyamnyam.coach.guild.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Guild chat create request")
public record GuildChatCreateRequest(
        @Schema(description = "채팅 메시지", example = "오늘 퀘스트 같이 완료해요!")
        @NotBlank(message = "채팅 메시지를 입력해주세요.")
        @Size(max = 1000, message = "채팅 메시지는 1000자 이하로 입력해주세요.")
        String message
) {
}
