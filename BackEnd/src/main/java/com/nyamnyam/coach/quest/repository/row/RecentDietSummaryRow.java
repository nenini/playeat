package com.nyamnyam.coach.quest.repository.row;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RecentDietSummaryRow {

    private Integer recordedDays;
    private Integer breakfastDays;
    private Integer lunchDays;
    private Integer dinnerDays;
    private Integer snackDays;
    private BigDecimal avgCalories;
    private BigDecimal avgProteinG;
    private BigDecimal avgCarbsG;
    private BigDecimal avgFatG;
    private BigDecimal avgSugarG;
    private BigDecimal avgSodiumMg;
    private BigDecimal avgFiberG;
    private BigDecimal targetCalories;
    private BigDecimal targetProteinG;
    private BigDecimal targetCarbsG;
    private BigDecimal targetFatG;
}
