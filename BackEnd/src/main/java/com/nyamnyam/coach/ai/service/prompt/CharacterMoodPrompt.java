package com.nyamnyam.coach.ai.service.prompt;

import java.time.LocalDate;
import java.util.List;

public record CharacterMoodPrompt(
        Long userId,
        LocalDate date,
        Integer healthScore,
        String summary,
        List<String> strengths,
        List<String> warnings,
        String nextAction
) {
}
