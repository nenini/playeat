package com.nyamnyam.coach.ai.service;

import com.nyamnyam.coach.diet.dto.response.DietDayResponse;
import com.nyamnyam.coach.diet.service.DietService;
import com.nyamnyam.coach.nutrition.dto.response.DailyNutritionAnalysisResponse;
import com.nyamnyam.coach.nutrition.dto.response.NutrientAnalysisResponse;
import com.nyamnyam.coach.nutrition.service.NutritionService;
import com.nyamnyam.coach.user.entity.HealthProfile;
import com.nyamnyam.coach.user.repository.HealthProfileRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WeeklyReportContextServiceTest {

    @Test
    void collectBuildsWeeklyContext() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2026, 6, 15);
        LocalDate endDate = LocalDate.of(2026, 6, 21);
        DietService dietService = mock(DietService.class);
        NutritionService nutritionService = mock(NutritionService.class);
        HealthProfileRepository healthProfileRepository = mock(HealthProfileRepository.class);

        when(dietService.getDietsByDate(eq(userId), any(LocalDate.class)))
                .thenAnswer(invocation -> new DietDayResponse(invocation.getArgument(1), List.of(), null));
        when(nutritionService.getDailyAnalysis(eq(userId), any(LocalDate.class)))
                .thenAnswer(invocation -> new DailyNutritionAnalysisResponse(
                        invocation.getArgument(1),
                        80,
                        List.of(new NutrientAnalysisResponse(
                                "SODIUM",
                                "나트륨",
                                "mg",
                                BigDecimal.valueOf(2500),
                                BigDecimal.valueOf(2000),
                                125,
                                "HIGH",
                                "나트륨이 높아요"
                        )),
                        List.of()
                ));
        when(healthProfileRepository.findByUserId(userId)).thenReturn(Optional.of(HealthProfile.builder()
                .userId(userId)
                .healthGoal("체중 관리")
                .activityLevel("보통")
                .targetSodiumMg(BigDecimal.valueOf(2000))
                .build()));

        WeeklyReportContextService service = new WeeklyReportContextService(
                dietService,
                nutritionService,
                healthProfileRepository
        );

        WeeklyReportContext context = service.collect(userId, startDate, endDate);

        assertThat(context.averageHealthScore()).isEqualTo(80);
        assertThat(context.dailyMealSummaries()).hasSize(7);
        assertThat(context.dailyNutritionSummaries()).hasSize(7);
        assertThat(context.repeatedPatterns()).anyMatch(pattern -> pattern.contains("나트륨"));
        assertThat(context.retrievalQuery()).contains("체중 관리", "나트륨");
    }
}
