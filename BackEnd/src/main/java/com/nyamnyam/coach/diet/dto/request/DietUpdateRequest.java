package com.nyamnyam.coach.diet.dto.request;

import com.nyamnyam.coach.diet.entity.MealType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record DietUpdateRequest(
        @NotNull(message = "끼니 유형은 필수입니다.")
        MealType mealType,

        @NotNull(message = "식사 시간은 필수입니다.")
        LocalDateTime eatenAt,

        String memo,

        @Valid
        @NotEmpty(message = "식단 음식은 1개 이상이어야 합니다.")
        List<DietItemRequest> items
) {
}
