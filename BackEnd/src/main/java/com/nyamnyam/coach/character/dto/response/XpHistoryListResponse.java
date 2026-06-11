package com.nyamnyam.coach.character.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Character XP history list response")
public record XpHistoryListResponse(
        List<XpHistoryResponse> content,
        Integer page,
        Integer size,
        Long totalElements,
        Boolean hasNext
) {
}
