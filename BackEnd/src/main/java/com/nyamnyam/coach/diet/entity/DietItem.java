package com.nyamnyam.coach.diet.entity;

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
public class DietItem {

    private Long dietItemId;
    private Long dietId;
    private Long foodId;
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
    private BigDecimal ironMg;
    private BigDecimal phosphorusMg;
    private BigDecimal potassiumMg;
    private BigDecimal vitaminAUgRae;
    private BigDecimal betaCaroteneUg;
    private BigDecimal retinolUg;
}
