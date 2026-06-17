package com.nyamnyam.coach.nutrition.repository.row;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class NutritionReferenceStandardRow {

    private String standardVersion;
    private BigDecimal sodiumMg;
    private BigDecimal fiberG;
}
