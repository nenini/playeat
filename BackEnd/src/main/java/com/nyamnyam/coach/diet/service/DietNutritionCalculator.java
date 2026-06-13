package com.nyamnyam.coach.diet.service;

import com.nyamnyam.coach.diet.dto.request.DietItemRequest;
import com.nyamnyam.coach.diet.entity.DietItem;
import com.nyamnyam.coach.food.entity.Food;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.DietErrorCode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@Component
public class DietNutritionCalculator {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    public DietItem calculate(DietItemRequest request, Food food) {
        String unit = normalizeUnit(request.inputUnit());
        BigDecimal inputAmount = request.inputAmount();
        if (inputAmount == null || inputAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(DietErrorCode.INVALID_AMOUNT);
        }

        BigDecimal amountG = null;
        BigDecimal amountMl = null;
        BigDecimal basisAmount;

        if ("g".equals(unit)) {
            amountG = inputAmount;
            if (!"g".equalsIgnoreCase(food.getNutritionBasisUnit())) {
                throw new BusinessException(DietErrorCode.FOOD_UNIT_CONVERSION_FAILED);
            }
            basisAmount = amountG;
        } else if ("ml".equals(unit)) {
            amountMl = inputAmount;
            if (!"ml".equalsIgnoreCase(food.getNutritionBasisUnit())) {
                throw new BusinessException(DietErrorCode.FOOD_UNIT_CONVERSION_FAILED);
            }
            basisAmount = amountMl;
        } else if ("piece".equals(unit)) {
            if (food.getGramPerPiece() == null || food.getGramPerPiece().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(DietErrorCode.FOOD_UNIT_CONVERSION_FAILED);
            }
            amountG = inputAmount.multiply(food.getGramPerPiece());
            if (!"g".equalsIgnoreCase(food.getNutritionBasisUnit())) {
                throw new BusinessException(DietErrorCode.FOOD_UNIT_CONVERSION_FAILED);
            }
            basisAmount = amountG;
        } else {
            throw new BusinessException(DietErrorCode.UNSUPPORTED_UNIT);
        }

        if (food.getNutritionBasisAmount() == null || food.getNutritionBasisAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(DietErrorCode.FOOD_UNIT_CONVERSION_FAILED);
        }

        BigDecimal ratio = basisAmount.divide(food.getNutritionBasisAmount(), 8, RoundingMode.HALF_UP);

        return DietItem.builder()
                .foodId(food.getFoodId())
                .inputAmount(scale(inputAmount))
                .inputUnit(unit)
                .amountG(scaleNullable(amountG))
                .amountMl(scaleNullable(amountMl))
                .calories(multiply(food.getCalories(), ratio))
                .proteinG(multiply(food.getProteinG(), ratio))
                .carbsG(multiply(food.getCarbsG(), ratio))
                .fatG(multiply(food.getFatG(), ratio))
                .sugarG(multiply(food.getSugarG(), ratio))
                .sodiumMg(multiply(food.getSodiumMg(), ratio))
                .fiberG(multiply(food.getFiberG(), ratio))
                .ironMg(multiply(food.getIronMg(), ratio))
                .phosphorusMg(multiply(food.getPhosphorusMg(), ratio))
                .potassiumMg(multiply(food.getPotassiumMg(), ratio))
                .vitaminAUgRae(multiply(food.getVitaminAUgRae(), ratio))
                .betaCaroteneUg(multiply(food.getBetaCaroteneUg(), ratio))
                .retinolUg(multiply(food.getRetinolUg(), ratio))
                .build();
    }

    private String normalizeUnit(String unit) {
        if (unit == null || unit.isBlank()) {
            throw new BusinessException(DietErrorCode.UNSUPPORTED_UNIT);
        }
        return unit.trim().toLowerCase(Locale.ROOT);
    }

    private BigDecimal multiply(BigDecimal value, BigDecimal ratio) {
        if (value == null) {
            return ZERO;
        }
        return scale(value.multiply(ratio));
    }

    private BigDecimal scaleNullable(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return scale(value);
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
