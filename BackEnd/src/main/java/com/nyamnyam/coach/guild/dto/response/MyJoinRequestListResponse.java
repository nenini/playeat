package com.nyamnyam.coach.guild.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "My join request list response")
public record MyJoinRequestListResponse(
        List<MyJoinRequestResponse> requests,
        Integer page,
        Integer size,
        Boolean hasNext
) {
}
