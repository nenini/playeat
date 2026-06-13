package com.nyamnyam.coach.diet.service;

import com.nyamnyam.coach.diet.dto.request.DietCreateRequest;
import com.nyamnyam.coach.diet.dto.request.DietItemRequest;
import com.nyamnyam.coach.diet.dto.request.DietUpdateRequest;
import com.nyamnyam.coach.diet.dto.response.DietDayResponse;
import com.nyamnyam.coach.diet.dto.response.DietDetailResponse;
import com.nyamnyam.coach.diet.entity.Diet;
import com.nyamnyam.coach.diet.entity.DietItem;
import com.nyamnyam.coach.diet.entity.MealType;
import com.nyamnyam.coach.diet.repository.DietRepository;
import com.nyamnyam.coach.diet.repository.row.DietItemRow;
import com.nyamnyam.coach.diet.repository.row.NutritionTargetRow;
import com.nyamnyam.coach.food.entity.Food;
import com.nyamnyam.coach.food.repository.FoodRepository;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.DietErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DietServiceTest {

    @Mock
    private DietRepository dietRepository;

    @Mock
    private FoodRepository foodRepository;

    private DietService dietService;

    @BeforeEach
    void setUp() {
        dietService = new DietService(
                dietRepository,
                foodRepository,
                new DietNutritionCalculator()
        );
    }

    @Test
    void getDietsByDateReturnsFourMealSlotsAndSummary() {
        LocalDate date = LocalDate.of(2026, 5, 15);
        Diet breakfast = diet(1L, MealType.BREAKFAST, new BigDecimal("304.00"));
        when(dietRepository.findByUserIdAndDate(
                eq(1L),
                eq(LocalDateTime.of(2026, 5, 15, 0, 0)),
                eq(LocalDateTime.of(2026, 5, 16, 0, 0))
        )).thenReturn(List.of(breakfast));
        when(dietRepository.findItemsByDietId(1L)).thenReturn(List.of(itemRow()));
        when(dietRepository.findNutritionTargetByUserId(1L)).thenReturn(Optional.of(targets()));

        DietDayResponse response = dietService.getDietsByDate(1L, date);

        assertThat(response.meals()).hasSize(4);
        assertThat(response.meals().get(0).recorded()).isTrue();
        assertThat(response.meals().get(0).items()).hasSize(1);
        assertThat(response.meals().get(3).recorded()).isFalse();
        assertThat(response.dailySummary().totalCalories()).isEqualByComparingTo("304.00");
        assertThat(response.dailySummary().calorieRate()).isEqualTo(15);
    }

    @Test
    void createDietCalculatesNutritionAndInsertsItems() {
        DietCreateRequest request = new DietCreateRequest(
                MealType.BREAKFAST,
                LocalDateTime.of(2026, 5, 15, 8, 10),
                null,
                List.of(new DietItemRequest(1L, new BigDecimal("80"), "g"))
        );
        when(dietRepository.findByUserIdAndDateAndMealType(any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food()));
        doAnswer(invocation -> {
            Diet diet = invocation.getArgument(0);
            diet.setDietId(1L);
            return null;
        }).when(dietRepository).insertDiet(any(Diet.class));
        when(dietRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(diet(1L, MealType.BREAKFAST, new BigDecimal("304.00"))));
        when(dietRepository.findItemsByDietId(1L)).thenReturn(List.of(itemRow()));

        DietDetailResponse response = dietService.createDiet(1L, request);

        assertThat(response.dietId()).isEqualTo(1L);
        assertThat(response.totalCalories()).isEqualByComparingTo("304.00");
        verify(dietRepository).insertDietItem(any(DietItem.class));
    }

    @Test
    void createDietRejectsDuplicateMealTypeOnSameDate() {
        DietCreateRequest request = new DietCreateRequest(
                MealType.BREAKFAST,
                LocalDateTime.of(2026, 5, 15, 8, 10),
                null,
                List.of(new DietItemRequest(1L, new BigDecimal("80"), "g"))
        );
        when(dietRepository.findByUserIdAndDateAndMealType(any(), any(), any(), any()))
                .thenReturn(Optional.of(diet(1L, MealType.BREAKFAST, BigDecimal.ZERO)));

        assertThatThrownBy(() -> dietService.createDiet(1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(DietErrorCode.DIET_ALREADY_EXISTS);
    }

    @Test
    void updateDietReplacesItemsAndRecalculatesTotals() {
        DietUpdateRequest request = new DietUpdateRequest(
                MealType.BREAKFAST,
                LocalDateTime.of(2026, 5, 15, 8, 20),
                "memo",
                List.of(new DietItemRequest(1L, new BigDecimal("100"), "g"))
        );
        Diet existing = diet(1L, MealType.BREAKFAST, new BigDecimal("304.00"));
        when(dietRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existing));
        when(dietRepository.findByUserIdAndDateAndMealType(any(), any(), any(), any()))
                .thenReturn(Optional.of(existing));
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food()));
        when(dietRepository.findItemsByDietId(1L)).thenReturn(List.of(itemRow()));

        DietDetailResponse response = dietService.updateDiet(1L, 1L, request);

        assertThat(response.dietId()).isEqualTo(1L);
        verify(dietRepository).deleteItemsByDietId(1L);
        verify(dietRepository).insertDietItem(any(DietItem.class));
        verify(dietRepository).updateDietTotals(existing);
    }

    private Diet diet(Long dietId, MealType mealType, BigDecimal calories) {
        return Diet.builder()
                .dietId(dietId)
                .userId(1L)
                .mealType(mealType)
                .eatenAt(LocalDateTime.of(2026, 5, 15, 8, 10))
                .totalCalories(calories)
                .totalProteinG(new BigDecimal("10.40"))
                .totalCarbsG(new BigDecimal("52.80"))
                .totalFatG(new BigDecimal("5.60"))
                .totalSugarG(new BigDecimal("0.20"))
                .totalSodiumMg(new BigDecimal("62.00"))
                .createdAt(LocalDateTime.of(2026, 5, 15, 8, 11))
                .updatedAt(LocalDateTime.of(2026, 5, 15, 8, 11))
                .build();
    }

    private DietItemRow itemRow() {
        return DietItemRow.builder()
                .dietItemId(10L)
                .dietId(1L)
                .foodId(1L)
                .foodName("오트밀")
                .inputAmount(new BigDecimal("80.00"))
                .inputUnit("g")
                .amountG(new BigDecimal("80.00"))
                .calories(new BigDecimal("304.00"))
                .proteinG(new BigDecimal("10.40"))
                .carbsG(new BigDecimal("52.80"))
                .fatG(new BigDecimal("5.60"))
                .sugarG(new BigDecimal("0.20"))
                .sodiumMg(new BigDecimal("62.00"))
                .build();
    }

    private NutritionTargetRow targets() {
        return NutritionTargetRow.builder()
                .targetCalories(new BigDecimal("2000.00"))
                .targetProteinG(new BigDecimal("90.00"))
                .targetCarbsG(new BigDecimal("280.00"))
                .targetFatG(new BigDecimal("65.00"))
                .build();
    }

    private Food food() {
        return Food.builder()
                .foodId(1L)
                .name("오트밀")
                .nutritionBasisAmount(new BigDecimal("100.00"))
                .nutritionBasisUnit("g")
                .calories(new BigDecimal("380.00"))
                .proteinG(new BigDecimal("13.00"))
                .carbsG(new BigDecimal("66.00"))
                .fatG(new BigDecimal("7.00"))
                .sugarG(new BigDecimal("0.25"))
                .sodiumMg(new BigDecimal("77.50"))
                .fiberG(BigDecimal.ZERO)
                .ironMg(BigDecimal.ZERO)
                .phosphorusMg(BigDecimal.ZERO)
                .potassiumMg(BigDecimal.ZERO)
                .vitaminAUgRae(BigDecimal.ZERO)
                .betaCaroteneUg(BigDecimal.ZERO)
                .retinolUg(BigDecimal.ZERO)
                .build();
    }
}
