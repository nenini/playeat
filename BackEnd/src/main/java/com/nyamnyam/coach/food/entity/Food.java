package com.nyamnyam.coach.food.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Food {

    private Long foodId;
    private String externalFoodCode;
    private String name;
    private String brand;
    private String category;
    private BigDecimal nutritionBasisAmount;
    private String nutritionBasisUnit;
    private BigDecimal servingAmount;
    private String servingUnit;
    private BigDecimal gramPerPiece;
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
    private String source;
    private LocalDateTime createdAt;

    public boolean isLiquidBasis() {
        return "ml".equalsIgnoreCase(nutritionBasisUnit);
    }

    public boolean hasServingAmount() {
        return servingAmount != null && servingUnit != null && !servingUnit.isBlank();
    }

    public boolean hasPieceAmount() {
        return gramPerPiece != null;
    }
}
