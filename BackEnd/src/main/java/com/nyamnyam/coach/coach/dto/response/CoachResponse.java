package com.nyamnyam.coach.coach.dto.response;

import com.nyamnyam.coach.coach.entity.Coach;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "코치 응답")
public record CoachResponse(
        Long coachId,
        String name,
        String role,
        String toneDescription,
        String sampleMessage,
        boolean selected
) {

    public static CoachResponse from(Coach coach, boolean selected) {
        return new CoachResponse(
                coach.getCoachId(),
                coach.getName(),
                coach.getRole(),
                coach.getToneDescription(),
                coach.getSampleMessage(),
                selected
        );
    }
}
