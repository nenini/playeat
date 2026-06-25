package com.nyamnyam.coach.analysis.dto.response;

import java.math.BigDecimal;

public record WeeklyNutritionAverageResponse(
        BigDecimal calories,
        BigDecimal protein,
        BigDecimal carbs,
        BigDecimal fat,
        BigDecimal sodium,
        BigDecimal fiber
) {
}
