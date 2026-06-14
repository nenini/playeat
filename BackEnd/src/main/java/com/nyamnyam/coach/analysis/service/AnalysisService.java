package com.nyamnyam.coach.analysis.service;

import com.nyamnyam.coach.ai.dto.response.AiReportResponse;
import com.nyamnyam.coach.ai.service.AiReportService;
import com.nyamnyam.coach.analysis.dto.response.AnalysisDailyResponse;
import com.nyamnyam.coach.coach.dto.response.CoachFeedbackResponse;
import com.nyamnyam.coach.coach.service.CoachService;
import com.nyamnyam.coach.diet.dto.response.DietDayResponse;
import com.nyamnyam.coach.diet.dto.response.DietMealResponse;
import com.nyamnyam.coach.diet.service.DietService;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.nutrition.dto.response.DailyNutritionAnalysisResponse;
import com.nyamnyam.coach.nutrition.service.NutritionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;

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
                diet,
                nutrition,
                dailyReport,
                latestMealFeedback
        );
    }

    private CoachFeedbackResponse findLatestMealFeedback(Long userId, DietDayResponse diet) {
        return diet.meals().stream()
                .filter(DietMealResponse::recorded)
                .filter(meal -> meal.dietId() != null)
                .max(Comparator.comparing(DietMealResponse::eatenAt))
                .map(meal -> getFeedbackOrNull(userId, meal.dietId()))
                .orElse(null);
    }

    private CoachFeedbackResponse getFeedbackOrNull(Long userId, Long dietId) {
        try {
            return coachService.getDietFeedback(userId, dietId);
        } catch (BusinessException e) {
            return null;
        }
    }
}
