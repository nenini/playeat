package com.nyamnyam.coach.nutrition.repository.row;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PeerNutritionStatRow {

    private String standardVersion;
    private String sourceName;
    private BigDecimal avgCalories;
    private BigDecimal avgProteinG;
    private BigDecimal avgCarbsG;
    private BigDecimal avgFatG;
    private BigDecimal avgSodiumMg;
    private BigDecimal avgFiberG;
}
