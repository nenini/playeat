package com.nyamnyam.coach.nutrition.repository.row;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DailyNutritionAggregateRow {

    private BigDecimal totalCalories;
    private BigDecimal totalProteinG;
    private BigDecimal totalCarbsG;
    private BigDecimal totalFatG;
    private BigDecimal totalSodiumMg;
    private BigDecimal totalFiberG;
    private BigDecimal targetCalories;
    private BigDecimal targetProteinG;
    private BigDecimal targetCarbsG;
    private BigDecimal targetFatG;
    private BigDecimal targetSodiumMg;
    private BigDecimal targetFiberG;
}
