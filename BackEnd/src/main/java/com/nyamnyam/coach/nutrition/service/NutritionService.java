package com.nyamnyam.coach.nutrition.service;

import com.nyamnyam.coach.nutrition.dto.response.DailyNutritionAnalysisResponse;
import com.nyamnyam.coach.nutrition.dto.response.NutrientAnalysisResponse;
import com.nyamnyam.coach.nutrition.dto.response.StandardInsightResponse;
import com.nyamnyam.coach.nutrition.repository.NutritionRepository;
import com.nyamnyam.coach.nutrition.repository.row.DailyNutritionAggregateRow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NutritionService {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_SODIUM_TARGET = BigDecimal.valueOf(2000).setScale(2, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_FIBER_TARGET = BigDecimal.valueOf(25).setScale(2, RoundingMode.HALF_UP);

    private final NutritionRepository nutritionRepository;

    public NutritionService(NutritionRepository nutritionRepository) {
        this.nutritionRepository = nutritionRepository;
    }

    @Transactional(readOnly = true)
    public DailyNutritionAnalysisResponse getDailyAnalysis(Long userId, LocalDate date) {
        DailyNutritionAggregateRow row = nutritionRepository.findDailyAggregate(userId, startOf(date), endOf(date));
        if (row == null) {
            row = new DailyNutritionAggregateRow();
        }

        List<NutrientAnalysisResponse> nutrients = List.of(
                nutrient("calories", "칼로리", "kcal", row.getTotalCalories(), row.getTargetCalories(), false),
                nutrient("protein", "단백질", "g", row.getTotalProteinG(), row.getTargetProteinG(), false),
                nutrient("carbs", "탄수화물", "g", row.getTotalCarbsG(), row.getTargetCarbsG(), false),
                nutrient("fat", "지방", "g", row.getTotalFatG(), row.getTargetFatG(), false),
                nutrient("sodium", "나트륨", "mg", row.getTotalSodiumMg(), defaultIfEmpty(row.getTargetSodiumMg(), DEFAULT_SODIUM_TARGET), true),
                nutrient("fiber", "식이섬유", "g", row.getTotalFiberG(), defaultIfEmpty(row.getTargetFiberG(), DEFAULT_FIBER_TARGET), false)
        );

        return new DailyNutritionAnalysisResponse(
                date,
                healthScore(nutrients),
                nutrients,
                standardInsights(nutrients)
        );
    }

    private NutrientAnalysisResponse nutrient(
            String code,
            String name,
            String unit,
            BigDecimal current,
            BigDecimal target,
            boolean lowerIsBetter
    ) {
        BigDecimal safeCurrent = zeroIfNull(current);
        BigDecimal safeTarget = zeroIfNull(target);
        int rate = rate(safeCurrent, safeTarget);
        String status = status(rate, lowerIsBetter);
        return new NutrientAnalysisResponse(
                code,
                name,
                unit,
                safeCurrent,
                safeTarget,
                rate,
                status
        );
    }

    private int healthScore(List<NutrientAnalysisResponse> nutrients) {
        return nutrients.stream()
                .mapToInt(nutrient -> weightedScore(nutrient, weight(nutrient.code())))
                .sum();
    }

    private int weightedScore(NutrientAnalysisResponse nutrient, int weight) {
        int rate = nutrient.achievementRate();
        if ("sodium".equals(nutrient.code())) {
            if (rate <= 100) {
                return weight;
            }
            if (rate <= 120) {
                return (int) Math.round(weight * 0.7);
            }
            if (rate <= 150) {
                return (int) Math.round(weight * 0.4);
            }
            return 0;
        }
        if (rate >= 80 && rate <= 120) {
            return weight;
        }
        if (rate >= 60 && rate < 80) {
            return (int) Math.round(weight * 0.7);
        }
        if (rate > 120 && rate <= 150) {
            return (int) Math.round(weight * 0.6);
        }
        if (rate > 0) {
            return (int) Math.round(weight * 0.3);
        }
        return 0;
    }

    private int weight(String code) {
        return switch (code) {
            case "calories" -> 25;
            case "protein" -> 20;
            case "carbs", "fat", "sodium" -> 15;
            case "fiber" -> 10;
            default -> 0;
        };
    }

    private List<StandardInsightResponse> standardInsights(List<NutrientAnalysisResponse> nutrients) {
        return nutrients.stream()
                .filter(nutrient -> !"OK".equals(nutrient.status()))
                .map(nutrient -> new StandardInsightResponse(
                        nutrient.code(),
                        nutrient.name(),
                        nutrient.unit(),
                        nutrient.current(),
                        nutrient.target(),
                        nutrient.status()
                ))
                .toList();
    }

    private String status(int rate, boolean lowerIsBetter) {
        if (lowerIsBetter) {
            return rate > 110 ? "HIGH" : "OK";
        }
        if (rate < 70) {
            return "LOW";
        }
        if (rate > 120) {
            return "HIGH";
        }
        return "OK";
    }

    private int rate(BigDecimal value, BigDecimal target) {
        if (target.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return value.multiply(BigDecimal.valueOf(100))
                .divide(target, 0, RoundingMode.HALF_UP)
                .intValue();
    }

    private BigDecimal defaultIfEmpty(BigDecimal value, BigDecimal defaultValue) {
        BigDecimal safeValue = zeroIfNull(value);
        if (safeValue.compareTo(BigDecimal.ZERO) <= 0) {
            return defaultValue;
        }
        return safeValue;
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private LocalDateTime startOf(LocalDate date) {
        return date.atStartOfDay();
    }

    private LocalDateTime endOf(LocalDate date) {
        return date.plusDays(1).atStartOfDay();
    }
}
