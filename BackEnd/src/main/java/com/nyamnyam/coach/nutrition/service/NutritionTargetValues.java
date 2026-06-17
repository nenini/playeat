package com.nyamnyam.coach.nutrition.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record NutritionTargetValues(
        BigDecimal calories,
        BigDecimal proteinG,
        BigDecimal carbsG,
        BigDecimal fatG,
        BigDecimal sodiumMg,
        BigDecimal fiberG,
        String standardVersion,
        LocalDateTime calculatedAt
) {
}
