package com.nyamnyam.coach.nutrition.service;

import com.nyamnyam.coach.nutrition.dto.response.DailyNutritionAnalysisResponse;
import com.nyamnyam.coach.nutrition.dto.response.NutrientAnalysisResponse;
import com.nyamnyam.coach.nutrition.dto.response.PeerComparisonResponse;
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
                nutrient("fiber", "식이섬유", "g", row.getTotalFiberG(), DEFAULT_FIBER_TARGET, false)
        );

        return new DailyNutritionAnalysisResponse(
                date,
                healthScore(nutrients),
                nutrients,
                peerComparison(nutrients)
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
                status,
                message(name, status, lowerIsBetter)
        );
    }

    private int healthScore(List<NutrientAnalysisResponse> nutrients) {
        int penalty = 0;
        for (NutrientAnalysisResponse nutrient : nutrients) {
            if ("LOW".equals(nutrient.status())) {
                penalty += 10;
            }
            if ("HIGH".equals(nutrient.status())) {
                penalty += 8;
            }
        }
        return Math.max(0, 100 - penalty);
    }

    private List<PeerComparisonResponse> peerComparison(List<NutrientAnalysisResponse> nutrients) {
        int userAverage = (int) nutrients.stream()
                .mapToInt(NutrientAnalysisResponse::achievementRate)
                .average()
                .orElse(0);
        int peerAverage = 78;
        return List.of(
                new PeerComparisonResponse(
                        "목표 달성률",
                        userAverage,
                        peerAverage,
                        userAverage >= peerAverage ? "또래 평균보다 목표 달성률이 높아요." : "또래 평균보다 조금 낮아요. 다음 끼니에서 부족한 영양소를 보완해보세요."
                )
        );
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

    private String message(String name, String status, boolean lowerIsBetter) {
        if ("LOW".equals(status)) {
            return name + " 섭취가 부족해요.";
        }
        if ("HIGH".equals(status)) {
            return lowerIsBetter ? name + " 섭취가 많아요." : name + " 섭취가 목표보다 높아요.";
        }
        return name + " 섭취가 적정 범위예요.";
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
