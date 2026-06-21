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
public class DietItemRow {

    private Long dietItemId;
    private Long dietId;
    private Long foodId;
    private String foodName;
    private String brand;
    private BigDecimal inputAmount;
    private String inputUnit;
    private BigDecimal amountG;
    private BigDecimal amountMl;
    private BigDecimal calories;
    private BigDecimal proteinG;
    private BigDecimal carbsG;
    private BigDecimal fatG;
    private BigDecimal sugarG;
    private BigDecimal sodiumMg;
    private BigDecimal fiberG;
}
