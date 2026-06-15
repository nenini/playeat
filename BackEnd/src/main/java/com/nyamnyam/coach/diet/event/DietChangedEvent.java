package com.nyamnyam.coach.diet.event;

import java.time.LocalDate;

public record DietChangedEvent(
        Long userId,
        LocalDate activityDate
) {
}
