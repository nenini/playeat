package com.nyamnyam.coach.nutrition.service;

import com.nyamnyam.coach.nutrition.repository.NutritionReferenceRepository;
import com.nyamnyam.coach.nutrition.repository.row.NutritionReferenceStandardRow;
import com.nyamnyam.coach.user.dto.request.HealthProfileRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Component
public class NutritionTargetCalculator {

    // Energy: KDRI 2020 adult EER equations for normal-weight adults.
    // Macros: representative targets inside KDRI 2020 energy intake ranges.
    // Sodium/fiber: KDRI table values are preferred; KDRI-labeled defaults are used only as fallback.
    private static final String DEFAULT_STANDARD_VERSION = "KDRI_2020";
    private static final BigDecimal DEFAULT_CALORIES = BigDecimal.valueOf(2000);
    private static final BigDecimal DEFAULT_SODIUM_MG = BigDecimal.valueOf(2000);
    private static final BigDecimal DEFAULT_FIBER_G = BigDecimal.valueOf(25);
    private static final BigDecimal MINIMUM_CALORIES = BigDecimal.valueOf(1200);
    // KDRI gives maintenance EER; weight-goal adjustment is a service policy applied after EER.
    private static final BigDecimal MAX_WEIGHT_GOAL_CALORIE_ADJUSTMENT = BigDecimal.valueOf(500);
    private static final BigDecimal LOSE_WEIGHT_ADJUSTMENT_RATIO = BigDecimal.valueOf(0.15);
    private static final BigDecimal GAIN_WEIGHT_ADJUSTMENT_RATIO = BigDecimal.valueOf(0.10);
    private static final BigDecimal CARBS_ENERGY_RATIO = BigDecimal.valueOf(0.60);
    private static final BigDecimal PROTEIN_ENERGY_RATIO = BigDecimal.valueOf(0.15);
    private static final BigDecimal FAT_ENERGY_RATIO = BigDecimal.valueOf(0.25);
    private static final BigDecimal CARBS_OR_PROTEIN_KCAL_PER_G = BigDecimal.valueOf(4);
    private static final BigDecimal FAT_KCAL_PER_G = BigDecimal.valueOf(9);

    private final NutritionReferenceRepository nutritionReferenceRepository;

    public NutritionTargetCalculator(NutritionReferenceRepository nutritionReferenceRepository) {
        this.nutritionReferenceRepository = nutritionReferenceRepository;
    }

    public NutritionTargetValues calculate(HealthProfileRequest request) {
        int age = age(request.birthDate());
        String gender = normalizeGender(request.gender());
        NutritionReferenceStandardRow standard = nutritionReferenceRepository
                .findStandard(gender, age)
                .orElse(null);

        BigDecimal calories = calculatedCalories(request, age, gender);
        BigDecimal protein = gramsFromEnergy(calories, PROTEIN_ENERGY_RATIO, CARBS_OR_PROTEIN_KCAL_PER_G);
        BigDecimal carbs = gramsFromEnergy(calories, CARBS_ENERGY_RATIO, CARBS_OR_PROTEIN_KCAL_PER_G);
        BigDecimal fat = gramsFromEnergy(calories, FAT_ENERGY_RATIO, FAT_KCAL_PER_G);

        return new NutritionTargetValues(
                scale(calories),
                scale(protein),
                scale(carbs),
                scale(fat),
                scale(standard == null ? DEFAULT_SODIUM_MG : defaultIfNull(standard.getSodiumMg(), DEFAULT_SODIUM_MG)),
                scale(standard == null ? DEFAULT_FIBER_G : defaultIfNull(standard.getFiberG(), DEFAULT_FIBER_G)),
                standard == null || standard.getStandardVersion() == null
                        ? DEFAULT_STANDARD_VERSION
                        : standard.getStandardVersion(),
                LocalDateTime.now()
        );
    }

    private BigDecimal calculatedCalories(HealthProfileRequest request, int age, String gender) {
        if (request.heightCm() == null || request.weightKg() == null || request.birthDate() == null) {
            return DEFAULT_CALORIES;
        }

        BigDecimal heightM = request.heightCm().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal calories;
        if ("FEMALE".equals(gender)) {
            calories = BigDecimal.valueOf(354)
                    .subtract(BigDecimal.valueOf(6.91).multiply(BigDecimal.valueOf(age)))
                    .add(activityCoefficient(request.activityLevel(), gender)
                            .multiply(BigDecimal.valueOf(9.36).multiply(request.weightKg())
                                    .add(BigDecimal.valueOf(726).multiply(heightM))));
        } else {
            calories = BigDecimal.valueOf(662)
                    .subtract(BigDecimal.valueOf(9.53).multiply(BigDecimal.valueOf(age)))
                    .add(activityCoefficient(request.activityLevel(), gender)
                            .multiply(BigDecimal.valueOf(15.91).multiply(request.weightKg())
                                    .add(BigDecimal.valueOf(539.6).multiply(heightM))));
        }

        if ("LOSE_WEIGHT".equalsIgnoreCase(request.healthGoal())) {
            calories = calories.subtract(weightGoalAdjustment(calories, LOSE_WEIGHT_ADJUSTMENT_RATIO));
        } else if ("GAIN_WEIGHT".equalsIgnoreCase(request.healthGoal())) {
            calories = calories.add(weightGoalAdjustment(calories, GAIN_WEIGHT_ADJUSTMENT_RATIO));
        }
        return calories.max(MINIMUM_CALORIES).setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal activityCoefficient(String activityLevel, String gender) {
        if (activityLevel == null) {
            return BigDecimal.ONE;
        }
        boolean female = "FEMALE".equals(gender);
        return switch (activityLevel.toUpperCase()) {
            case "LIGHT" -> female ? BigDecimal.valueOf(1.12) : BigDecimal.valueOf(1.11);
            case "MODERATE" -> female ? BigDecimal.valueOf(1.27) : BigDecimal.valueOf(1.25);
            case "ACTIVE", "VERY_ACTIVE" -> female ? BigDecimal.valueOf(1.45) : BigDecimal.valueOf(1.48);
            default -> BigDecimal.ONE;
        };
    }

    private int age(LocalDate birthDate) {
        if (birthDate == null) {
            return 30;
        }
        return Math.max(1, Period.between(birthDate, LocalDate.now()).getYears());
    }

    private String normalizeGender(String gender) {
        if (gender == null || gender.isBlank()) {
            return "ALL";
        }
        String normalized = gender.toUpperCase();
        if ("MALE".equals(normalized) || "FEMALE".equals(normalized)) {
            return normalized;
        }
        return "ALL";
    }

    private BigDecimal defaultIfNull(BigDecimal value, BigDecimal defaultValue) {
        return value == null ? defaultValue : value;
    }

    private BigDecimal gramsFromEnergy(BigDecimal calories, BigDecimal energyRatio, BigDecimal kcalPerGram) {
        return calories.multiply(energyRatio)
                .divide(kcalPerGram, 0, RoundingMode.HALF_UP);
    }

    private BigDecimal weightGoalAdjustment(BigDecimal calories, BigDecimal ratio) {
        return calories.multiply(ratio).min(MAX_WEIGHT_GOAL_CALORIE_ADJUSTMENT);
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
