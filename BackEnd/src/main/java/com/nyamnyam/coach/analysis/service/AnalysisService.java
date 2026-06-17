package com.nyamnyam.coach.analysis.service;

import com.nyamnyam.coach.ai.dto.response.AiReportResponse;
import com.nyamnyam.coach.ai.service.AiReportService;
import com.nyamnyam.coach.analysis.dto.response.AnalysisDailyResponse;
import com.nyamnyam.coach.analysis.dto.response.AnalysisDailyScoreResponse;
import com.nyamnyam.coach.analysis.dto.response.AnalysisWeeklyResponse;
import com.nyamnyam.coach.analysis.dto.response.WeeklyNutritionAverageResponse;
import com.nyamnyam.coach.coach.dto.response.CoachFeedbackResponse;
import com.nyamnyam.coach.coach.service.CoachService;
import com.nyamnyam.coach.diet.dto.response.DietDayResponse;
import com.nyamnyam.coach.diet.dto.response.DietMealResponse;
import com.nyamnyam.coach.diet.service.DietService;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AiErrorCode;
import com.nyamnyam.coach.nutrition.dto.response.DailyNutritionAnalysisResponse;
import com.nyamnyam.coach.nutrition.service.NutritionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AnalysisService {

    private final DietService dietService;
    private final NutritionService nutritionService;
    private final CoachService coachService;
    private final AiReportService aiReportService;

    public AnalysisService(
            DietService dietService,
            NutritionService nutritionService,
            CoachService coachService,
            AiReportService aiReportService
    ) {
        this.dietService = dietService;
        this.nutritionService = nutritionService;
        this.coachService = coachService;
        this.aiReportService = aiReportService;
    }

    @Transactional(readOnly = true)
    public AnalysisDailyResponse getDailyAnalysis(Long userId, LocalDate date) {
        DietDayResponse diet = dietService.getDietsByDate(userId, date);
        DailyNutritionAnalysisResponse nutrition = nutritionService.getDailyAnalysis(userId, date);
        AiReportResponse dailyReport = aiReportService.findDailyReportOrNull(userId, date);
        CoachFeedbackResponse latestMealFeedback = findLatestMealFeedback(userId, diet);

        return new AnalysisDailyResponse(
                date,
                nutrition.healthScore(),
                diet,
                nutrition,
                dailyReport,
                latestMealFeedback
        );
    }

    @Transactional(readOnly = true)
    public AnalysisWeeklyResponse getWeeklyAnalysis(Long userId, LocalDate startDate, LocalDate endDate) {
        validateWeeklyPeriod(startDate, endDate);

        List<AnalysisDailyResponse> dailyResponses = collectDailyResponses(userId, startDate, endDate);
        int currentAverage = average(dailyResponses.stream()
                .map(AnalysisDailyResponse::healthScore)
                .toList());
        int previousAverage = averageHealthScore(userId, startDate.minusDays(7), endDate.minusDays(7));
        int recordedDays = (int) dailyResponses.stream()
                .filter(response -> recordedMealCount(response.diet()) > 0)
                .count();
        int totalDays = dailyResponses.size();

        return new AnalysisWeeklyResponse(
                startDate,
                endDate,
                currentAverage,
                currentAverage - previousAverage,
                totalDays == 0 ? 0 : (int) Math.round((recordedDays * 100.0) / totalDays),
                recordedDays,
                totalDays,
                dailyResponses.stream()
                        .map(response -> new AnalysisDailyScoreResponse(
                                response.date(),
                                response.date().getDayOfWeek().name(),
                                response.healthScore()
                        ))
                        .toList(),
                weeklyAverage(dailyResponses),
                aiReportService.findWeeklyReportOrNull(userId, startDate, endDate)
        );
    }

    private List<AnalysisDailyResponse> collectDailyResponses(Long userId, LocalDate startDate, LocalDate endDate) {
        List<AnalysisDailyResponse> responses = new ArrayList<>();
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            responses.add(getDailyAnalysis(userId, date));
            date = date.plusDays(1);
        }
        return responses;
    }

    private int averageHealthScore(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Integer> scores = new ArrayList<>();
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            scores.add(nutritionService.getDailyAnalysis(userId, date).healthScore());
            date = date.plusDays(1);
        }
        return average(scores);
    }

    private int average(List<Integer> values) {
        if (values.isEmpty()) {
            return 0;
        }
        return (int) Math.round(values.stream().mapToInt(Integer::intValue).average().orElse(0));
    }

    private WeeklyNutritionAverageResponse weeklyAverage(List<AnalysisDailyResponse> dailyResponses) {
        int count = dailyResponses.isEmpty() ? 1 : dailyResponses.size();
        return new WeeklyNutritionAverageResponse(
                averageNutrient(dailyResponses, "calories", count),
                averageNutrient(dailyResponses, "protein", count),
                averageNutrient(dailyResponses, "carbs", count),
                averageNutrient(dailyResponses, "fat", count),
                averageNutrient(dailyResponses, "sodium", count),
                averageNutrient(dailyResponses, "fiber", count)
        );
    }

    private BigDecimal averageNutrient(List<AnalysisDailyResponse> dailyResponses, String code, int count) {
        BigDecimal total = dailyResponses.stream()
                .flatMap(response -> response.nutrition().nutrients().stream())
                .filter(nutrient -> code.equals(nutrient.code()))
                .map(nutrient -> nutrient.current() == null ? BigDecimal.ZERO : nutrient.current())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    private int recordedMealCount(DietDayResponse diet) {
        if (diet == null || diet.meals() == null) {
            return 0;
        }
        return (int) diet.meals().stream().filter(DietMealResponse::recorded).count();
    }

    private void validateWeeklyPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null
                || startDate.getDayOfWeek() != DayOfWeek.MONDAY
                || !endDate.equals(startDate.plusDays(6))) {
            throw new BusinessException(AiErrorCode.AI_REPORT_PERIOD_INVALID);
        }
    }

    private CoachFeedbackResponse findLatestMealFeedback(Long userId, DietDayResponse diet) {
        if (diet == null || diet.meals() == null) {
            return null;
        }
        return diet.meals().stream()
                .filter(DietMealResponse::recorded)
                .filter(meal -> meal.dietId() != null)
                .max(Comparator.comparing(DietMealResponse::eatenAt))
                .flatMap(meal -> coachService.findDietFeedback(userId, meal.dietId()))
                .orElse(null);
    }
}
