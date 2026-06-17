package com.nyamnyam.coach.diet.repository.row;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionTargetRow {

    private BigDecimal targetCalories;
    private BigDecimal targetProteinG;
    private BigDecimal targetCarbsG;
    private BigDecimal targetFatG;
    private BigDecimal targetFiberG;
}
