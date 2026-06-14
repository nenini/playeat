package com.nyamnyam.coach.ai.service.prompt;

import java.math.BigDecimal;

public record CoachFeedbackPrompt(
        String coachName,
        String coachTone,
        BigDecimal calories,
        BigDecimal proteinG,
        BigDecimal carbsG,
        BigDecimal fatG
) {
}
