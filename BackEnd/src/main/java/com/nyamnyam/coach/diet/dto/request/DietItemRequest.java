package com.nyamnyam.coach.diet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record DietItemRequest(
        @Schema(description = "음식 ID", example = "1")
        @NotNull(message = "음식 ID는 필수입니다.")
        Long foodId,

        @Schema(description = "입력량", example = "80")
        @NotNull(message = "입력량은 필수입니다.")
        @DecimalMin(value = "0.01", message = "입력량은 0보다 커야 합니다.")
        BigDecimal inputAmount,

        @Schema(description = "입력 단위", example = "g")
        @NotBlank(message = "입력 단위는 필수입니다.")
        String inputUnit
) {
}
