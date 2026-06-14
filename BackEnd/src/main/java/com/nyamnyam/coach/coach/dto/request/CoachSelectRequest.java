package com.nyamnyam.coach.coach.dto.request;

import jakarta.validation.constraints.NotNull;

public record CoachSelectRequest(
        @NotNull Long coachId
) {
}
