package com.nyamnyam.coach.coach.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "코치 목록 응답")
public record CoachListResponse(
        List<CoachResponse> coaches
) {
}
