package com.nyamnyam.coach.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Health profile response")
public record HealthProfileResponse(
        @Schema(description = "Health profile id", example = "1")
        Long healthProfileId,

        @Schema(description = "Height in centimeters", example = "162")
        BigDecimal heightCm,

        @Schema(description = "Current weight in kilograms", example = "54")
        BigDecimal weightKg,

        @Schema(description = "Target weight in kilograms", example = "50")
        BigDecimal targetWeightKg,

        @Schema(description = "Birth date", example = "2001-03-15")
        LocalDate birthDate,

        @Schema(description = "Gender", example = "FEMALE")
        String gender,

        @Schema(description = "Health goal", example = "LOSE_WEIGHT")
        String healthGoal,

        @Schema(description = "Activity level", example = "LIGHT")
        String activityLevel,

        @Schema(description = "Diet styles", example = "[\"BALANCED\"]")
        List<String> dietStyles,

        @Schema(description = "Restricted foods", example = "[\"CAFFEINE\"]")
        List<String> restrictedFoods,

        @Schema(description = "Allergies", example = "[\"PEANUT\"]")
        List<String> allergies,

        @Schema(description = "Target calories", example = "2000")
        BigDecimal targetCalories,

        @Schema(description = "Target protein in grams", example = "90")
        BigDecimal targetProteinG,

        @Schema(description = "Target carbs in grams", example = "260")
        BigDecimal targetCarbsG,

        @Schema(description = "Target fat in grams", example = "65")
        BigDecimal targetFatG,

        @Schema(description = "Target sodium in milligrams", example = "2300")
        BigDecimal targetSodiumMg,

        @Schema(description = "Updated at", example = "2026-06-09T12:00:00")
        LocalDateTime updatedAt
) {
}
