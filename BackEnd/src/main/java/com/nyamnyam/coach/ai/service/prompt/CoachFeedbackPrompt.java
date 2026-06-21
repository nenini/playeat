package com.nyamnyam.coach.ai.service.prompt;

import java.math.BigDecimal;

public record CoachFeedbackPrompt(
        String coachName,
        String coachTone,
        String mealType,
        String mealItems,
        BigDecimal calories,
        BigDecimal proteinG,
        BigDecimal carbsG,
        BigDecimal fatG,
        BigDecimal sodiumMg,
        BigDecimal fiberG,
        String caution
) {
}
