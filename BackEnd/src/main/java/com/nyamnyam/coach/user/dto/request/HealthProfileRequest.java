package com.nyamnyam.coach.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Health profile request")
public record HealthProfileRequest(
        @Schema(description = "Height in centimeters", example = "162")
        @NotNull(message = "Height is required.")
        @DecimalMin(value = "0.0", inclusive = false, message = "Height must be greater than 0.")
        BigDecimal heightCm,

        @Schema(description = "Current weight in kilograms", example = "54")
        @NotNull(message = "Weight is required.")
        @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be greater than 0.")
        BigDecimal weightKg,

        @Schema(description = "Target weight in kilograms", example = "50")
        @DecimalMin(value = "0.0", inclusive = false, message = "Target weight must be greater than 0.")
        BigDecimal targetWeightKg,

        @Schema(description = "Birth date", example = "2001-03-15")
        @NotNull(message = "Birth date is required.")
        @Past(message = "Birth date must be in the past.")
        LocalDate birthDate,

        @Schema(description = "Gender", example = "FEMALE")
        @NotBlank(message = "Gender is required.")
        @Size(max = 20, message = "Gender must be 20 characters or less.")
        String gender,

        @Schema(description = "Health goal", example = "LOSE_WEIGHT")
        @NotBlank(message = "Health goal is required.")
        @Size(max = 30, message = "Health goal must be 30 characters or less.")
        String healthGoal,

        @Schema(description = "Activity level", example = "LIGHT")
        @NotBlank(message = "Activity level is required.")
        @Size(max = 30, message = "Activity level must be 30 characters or less.")
        String activityLevel,

        @Schema(description = "Diet styles", example = "[\"BALANCED\"]")
        List<String> dietStyles,

        @Schema(description = "Restricted foods", example = "[\"CAFFEINE\"]")
        List<String> restrictedFoods,

        @Schema(description = "Allergies", example = "[\"PEANUT\"]")
        List<String> allergies
) {
}
