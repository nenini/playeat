package com.nyamnyam.coach.diet.entity;

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
public class Diet {

    private Long dietId;
    private Long userId;
    private MealType mealType;
    private LocalDateTime eatenAt;
    private String memo;
    private BigDecimal totalCalories;
    private BigDecimal totalProteinG;
    private BigDecimal totalCarbsG;
    private BigDecimal totalFatG;
    private BigDecimal totalSugarG;
    private BigDecimal totalSodiumMg;
    private BigDecimal totalFiberG;
    private BigDecimal totalIronMg;
    private BigDecimal totalPhosphorusMg;
    private BigDecimal totalPotassiumMg;
    private BigDecimal totalVitaminAUgRae;
    private BigDecimal totalBetaCaroteneUg;
    private BigDecimal totalRetinolUg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
