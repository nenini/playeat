package com.nyamnyam.coach.ai.service;

import com.nyamnyam.coach.diet.dto.response.DietDayResponse;
import com.nyamnyam.coach.diet.dto.response.DietItemResponse;
import com.nyamnyam.coach.diet.dto.response.DietMealResponse;
import com.nyamnyam.coach.diet.service.DietService;
import com.nyamnyam.coach.nutrition.dto.response.DailyNutritionAnalysisResponse;
import com.nyamnyam.coach.nutrition.dto.response.NutrientAnalysisResponse;
import com.nyamnyam.coach.nutrition.service.NutritionService;
import com.nyamnyam.coach.user.entity.HealthProfile;
import com.nyamnyam.coach.user.repository.HealthProfileRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class WeeklyReportContextService {

    private final DietService dietService;
    private final NutritionService nutritionService;
    private final HealthProfileRepository healthProfileRepository;

    public WeeklyReportContextService(
            DietService dietService,
            NutritionService nutritionService,
            HealthProfileRepository healthProfileRepository
    ) {
        this.dietService = dietService;
        this.nutritionService = nutritionService;
        this.healthProfileRepository = healthProfileRepository;
    }

    public WeeklyReportContext collect(Long userId, LocalDate startDate, LocalDate endDate) {
        List<String> mealSummaries = new ArrayList<>();
        List<String> nutritionSummaries = new ArrayList<>();
        List<String> repeatedPatterns = new ArrayList<>();
        List<Integer> healthScores = new ArrayList<>();
        List<NutrientAnalysisResponse> allNutrients = new ArrayList<>();

        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            mealSummaries.add(collectMealSummary(userId, date));
            DailyNutritionAnalysisResponse analysis = collectNutritionAnalysis(userId, date);
            if (analysis != null) {
                healthScores.add(analysis.healthScore());
                if (analysis.nutrients() != null) {
                    allNutrients.addAll(analysis.nutrients());
                }
                nutritionSummaries.add(toNutritionSummary(analysis));
            } else {
                nutritionSummaries.add("%s: 영양 분석 데이터 없음".formatted(date));
            }
            date = date.plusDays(1);
        }

        repeatedPatterns.addAll(repeatedNutrientPatterns(allNutrients));
        if (recordedDayCount(mealSummaries) < 4) {
            repeatedPatterns.add("주간 식단 기록일이 4일 미만이라 분석 신뢰도가 낮습니다.");
        }
        if (repeatedPatterns.isEmpty()) {
            repeatedPatterns.add("이번 주는 뚜렷하게 반복된 위험 패턴보다 일별 편차를 중심으로 확인이 필요합니다.");
        }

        String healthProfileSummary = healthProfileRepository.findByUserId(userId)
                .map(this::toHealthProfileSummary)
                .orElse("건강 프로필 미등록: 기본 영양 분석 결과와 식단 기록 중심으로 리포트를 생성합니다.");

        int averageHealthScore = healthScores.isEmpty()
                ? 0
                : (int) Math.round(healthScores.stream().mapToInt(Integer::intValue).average().orElse(0));

        String retrievalQuery = buildRetrievalQuery(healthProfileSummary, nutritionSummaries, repeatedPatterns);
        return new WeeklyReportContext(
                averageHealthScore,
                healthProfileSummary,
                mealSummaries,
                nutritionSummaries,
                repeatedPatterns,
                retrievalQuery
        );
    }

    private String collectMealSummary(Long userId, LocalDate date) {
        try {
            DietDayResponse dietDay = dietService.getDietsByDate(userId, date);
            List<DietMealResponse> meals = dietDay.meals() == null ? List.of() : dietDay.meals();
            List<DietMealResponse> recordedMeals = meals.stream()
                    .filter(DietMealResponse::recorded)
                    .toList();
            if (recordedMeals.isEmpty()) {
                return "%s: 기록된 식단 없음".formatted(date);
            }
            String mealText = recordedMeals.stream()
                    .map(this::toMealSummary)
                    .reduce((left, right) -> left + " / " + right)
                    .orElse("기록된 식단 없음");
            return "%s: %s".formatted(date, mealText);
        } catch (Exception exception) {
            return "%s: 식단 조회 실패".formatted(date);
        }
    }

    private DailyNutritionAnalysisResponse collectNutritionAnalysis(Long userId, LocalDate date) {
        try {
            return nutritionService.getDailyAnalysis(userId, date);
        } catch (Exception exception) {
            return null;
        }
    }

    private String toMealSummary(DietMealResponse meal) {
        List<DietItemResponse> mealItems = meal.items() == null ? List.of() : meal.items();
        String items = mealItems.stream()
                .filter(java.util.Objects::nonNull)
                .map(this::toItemSummary)
                .reduce((left, right) -> left + ", " + right)
                .orElse("기록된 음식 없음");
        return "%s: %s".formatted(meal.label(), items);
    }

    private String toItemSummary(DietItemResponse item) {
        return "%s %s%s".formatted(item.foodName(), item.inputAmount(), item.inputUnit());
    }

    private String toNutritionSummary(DailyNutritionAnalysisResponse analysis) {
        List<NutrientAnalysisResponse> nutrients = analysis.nutrients() == null ? List.of() : analysis.nutrients();
        String nutrientSummary = nutrients.stream()
                .map(nutrient -> "%s %s%%(%s)".formatted(nutrient.name(), nutrient.achievementRate(), nutrient.status()))
                .reduce((left, right) -> left + ", " + right)
                .orElse("영양소 분석 없음");
        return "%s: 건강점수 %d, %s".formatted(analysis.date(), analysis.healthScore(), nutrientSummary);
    }

    private String toHealthProfileSummary(HealthProfile profile) {
        return "목표=%s, 활동량=%s, 키=%scm, 체중=%skg, 목표체중=%skg, 성별=%s, 제한식품=%s, 알레르기=%s, 목표열량=%skcal, 목표단백질=%sg, 목표탄수화물=%sg, 목표지방=%sg, 목표나트륨=%smg"
                .formatted(
                        nullToDash(profile.getHealthGoal()),
                        nullToDash(profile.getActivityLevel()),
                        nullToDash(profile.getHeightCm()),
                        nullToDash(profile.getWeightKg()),
                        nullToDash(profile.getTargetWeightKg()),
                        nullToDash(profile.getGender()),
                        nullToDash(profile.getRestrictedFoodsJson()),
                        nullToDash(profile.getAllergiesJson()),
                        nullToDash(profile.getTargetCalories()),
                        nullToDash(profile.getTargetProteinG()),
                        nullToDash(profile.getTargetCarbsG()),
                        nullToDash(profile.getTargetFatG()),
                        nullToDash(profile.getTargetSodiumMg())
                );
    }

    private List<String> repeatedNutrientPatterns(List<NutrientAnalysisResponse> nutrients) {
        return nutrients.stream()
                .filter(nutrient -> isWarningStatus(nutrient.status()))
                .collect(java.util.stream.Collectors.groupingBy(
                        NutrientAnalysisResponse::name,
                        java.util.stream.Collectors.counting()
                ))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= 2)
                .sorted(Comparator.comparingLong(entry -> -entry.getValue()))
                .map(entry -> "%s 관련 주의 상태가 %d일 반복되었습니다.".formatted(entry.getKey(), entry.getValue()))
                .limit(5)
                .toList();
    }

    private boolean isWarningStatus(String status) {
        if (status == null) {
            return false;
        }
        String normalized = status.toUpperCase();
        return normalized.contains("LOW")
                || normalized.contains("HIGH")
                || normalized.contains("OVER")
                || normalized.contains("UNDER")
                || normalized.contains("주의")
                || normalized.contains("부족")
                || normalized.contains("과다");
    }

    private int recordedDayCount(List<String> mealSummaries) {
        return (int) mealSummaries.stream()
                .filter(summary -> !summary.contains("기록된 식단 없음") && !summary.contains("식단 조회 실패"))
                .count();
    }

    private String buildRetrievalQuery(
            String healthProfileSummary,
            List<String> nutritionSummaries,
            List<String> repeatedPatterns
    ) {
        return """
                주간 식단 리포트 공식 건강 자료 검색
                건강 프로필: %s
                반복 패턴: %s
                영양 요약: %s
                나트륨 당류 단백질 채소 균형 식사 만성질환 예방 체중관리
                """.formatted(
                healthProfileSummary,
                String.join(" / ", repeatedPatterns),
                String.join(" / ", nutritionSummaries)
        );
    }

    private String nullToDash(Object value) {
        if (value == null) {
            return "-";
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.stripTrailingZeros().toPlainString();
        }
        return value.toString();
    }
}
