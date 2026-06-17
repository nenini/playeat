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

    private static final String DEFAULT_STANDARD_VERSION = "KDRI_2020";
    private static final BigDecimal DEFAULT_CALORIES = BigDecimal.valueOf(2000);
    private static final BigDecimal DEFAULT_PROTEIN_G = BigDecimal.valueOf(90);
    private static final BigDecimal DEFAULT_CARBS_G = BigDecimal.valueOf(260);
    private static final BigDecimal DEFAULT_FAT_G = BigDecimal.valueOf(65);
    private static final BigDecimal DEFAULT_SODIUM_MG = BigDecimal.valueOf(2000);
    private static final BigDecimal DEFAULT_FIBER_G = BigDecimal.valueOf(25);

    private final NutritionReferenceRepository nutritionReferenceRepository;

    public NutritionTargetCalculator(NutritionReferenceRepository nutritionReferenceRepository) {
        this.nutritionReferenceRepository = nutritionReferenceRepository;
    }

    public NutritionTargetValues calculate(HealthProfileRequest request) {
        int age = age(request.birthDate());
        NutritionReferenceStandardRow standard = nutritionReferenceRepository
                .findStandard(normalizeGender(request.gender()), age)
                .orElse(null);

        BigDecimal calories = calculatedCalories(request);
        BigDecimal protein = calories.multiply(BigDecimal.valueOf(0.18))
                .divide(BigDecimal.valueOf(4), 0, RoundingMode.HALF_UP);
        BigDecimal carbs = calories.multiply(BigDecimal.valueOf(0.52))
                .divide(BigDecimal.valueOf(4), 0, RoundingMode.HALF_UP);
        BigDecimal fat = calories.multiply(BigDecimal.valueOf(0.30))
                .divide(BigDecimal.valueOf(9), 0, RoundingMode.HALF_UP);

        return new NutritionTargetValues(
                scale(defaultIfNull(calories, DEFAULT_CALORIES)),
                scale(defaultIfNull(protein, DEFAULT_PROTEIN_G)),
                scale(defaultIfNull(carbs, DEFAULT_CARBS_G)),
                scale(defaultIfNull(fat, DEFAULT_FAT_G)),
                scale(standard == null ? DEFAULT_SODIUM_MG : defaultIfNull(standard.getSodiumMg(), DEFAULT_SODIUM_MG)),
                scale(standard == null ? DEFAULT_FIBER_G : defaultIfNull(standard.getFiberG(), DEFAULT_FIBER_G)),
                standard == null || standard.getStandardVersion() == null
                        ? DEFAULT_STANDARD_VERSION
                        : standard.getStandardVersion(),
                LocalDateTime.now()
        );
    }

    private BigDecimal calculatedCalories(HealthProfileRequest request) {
        if (request.heightCm() == null || request.weightKg() == null || request.birthDate() == null) {
            return DEFAULT_CALORIES;
        }

        int age = age(request.birthDate());
        BigDecimal bmr = request.weightKg().multiply(BigDecimal.valueOf(10))
                .add(request.heightCm().multiply(BigDecimal.valueOf(6.25)))
                .subtract(BigDecimal.valueOf(age * 5L));

        if ("FEMALE".equalsIgnoreCase(request.gender())) {
            bmr = bmr.subtract(BigDecimal.valueOf(161));
        } else {
            bmr = bmr.add(BigDecimal.valueOf(5));
        }

        BigDecimal calories = bmr.multiply(activityFactor(request.activityLevel()));
        if ("LOSE_WEIGHT".equalsIgnoreCase(request.healthGoal())) {
            calories = calories.subtract(BigDecimal.valueOf(300));
        } else if ("GAIN_WEIGHT".equalsIgnoreCase(request.healthGoal())) {
            calories = calories.add(BigDecimal.valueOf(300));
        }
        return calories.max(BigDecimal.valueOf(1200)).setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal activityFactor(String activityLevel) {
        if (activityLevel == null) {
            return BigDecimal.valueOf(1.2);
        }
        return switch (activityLevel.toUpperCase()) {
            case "LIGHT" -> BigDecimal.valueOf(1.375);
            case "MODERATE" -> BigDecimal.valueOf(1.55);
            case "ACTIVE" -> BigDecimal.valueOf(1.725);
            case "VERY_ACTIVE" -> BigDecimal.valueOf(1.9);
            default -> BigDecimal.valueOf(1.2);
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

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
