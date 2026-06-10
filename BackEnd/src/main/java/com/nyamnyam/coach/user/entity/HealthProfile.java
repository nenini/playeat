package com.nyamnyam.coach.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthProfile {

    private Long healthProfileId;
    private Long userId;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private BigDecimal targetWeightKg;
    private LocalDate birthDate;
    private String gender;
    private String healthGoal;
    private String activityLevel;
    private String dietStylesJson;
    private String restrictedFoodsJson;
    private String allergiesJson;
    private BigDecimal targetCalories;
    private BigDecimal targetProteinG;
    private BigDecimal targetCarbsG;
    private BigDecimal targetFatG;
    private BigDecimal targetSodiumMg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
