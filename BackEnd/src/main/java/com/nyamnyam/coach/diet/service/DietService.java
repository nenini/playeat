package com.nyamnyam.coach.diet.service;

import com.nyamnyam.coach.diet.dto.request.DietCreateRequest;
import com.nyamnyam.coach.diet.dto.request.DietItemRequest;
import com.nyamnyam.coach.diet.dto.request.DietUpdateRequest;
import com.nyamnyam.coach.diet.dto.response.DailyNutritionSummaryResponse;
import com.nyamnyam.coach.diet.dto.response.DietDayResponse;
import com.nyamnyam.coach.diet.dto.response.DietDeleteResponse;
import com.nyamnyam.coach.diet.dto.response.DietDetailResponse;
import com.nyamnyam.coach.diet.dto.response.DietItemResponse;
import com.nyamnyam.coach.diet.dto.response.DietMealResponse;
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
import com.nyamnyam.coach.global.exception.errorcode.FoodErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class DietService {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final int TARGET_VEGETABLE_SERVINGS = 2;

    private final DietRepository dietRepository;
    private final FoodRepository foodRepository;
    private final DietNutritionCalculator nutritionCalculator;

    public DietService(
            DietRepository dietRepository,
            FoodRepository foodRepository,
            DietNutritionCalculator nutritionCalculator
    ) {
        this.dietRepository = dietRepository;
        this.foodRepository = foodRepository;
        this.nutritionCalculator = nutritionCalculator;
    }

    @Transactional(readOnly = true)
    public DietDayResponse getDietsByDate(Long userId, LocalDate date) {
        List<Diet> diets = dietRepository.findByUserIdAndDate(userId, startOf(date), endOf(date));
        Map<MealType, Diet> dietByMealType = new EnumMap<>(MealType.class);
        for (Diet diet : diets) {
            dietByMealType.put(diet.getMealType(), diet);
        }

        List<DietMealResponse> meals = new ArrayList<>();
        for (MealType mealType : MealType.values()) {
            Diet diet = dietByMealType.get(mealType);
            if (diet == null) {
                meals.add(emptyMeal(mealType));
            } else {
                meals.add(toMealResponse(diet));
            }
        }

        return new DietDayResponse(date, meals, buildDailySummary(userId, diets));
    }

    @Transactional
    public DietDetailResponse createDiet(Long userId, DietCreateRequest request) {
        validateItems(request.items());
        LocalDate date = request.eatenAt().toLocalDate();
        assertNotDuplicated(userId, date, request.mealType(), null);

        List<DietItem> items = calculateItems(request.items());
        Diet diet = Diet.builder()
                .userId(userId)
                .mealType(request.mealType())
                .eatenAt(request.eatenAt())
                .memo(request.memo())
                .build();
        applyTotals(diet, items);
        dietRepository.insertDiet(diet);
        for (DietItem item : items) {
            item.setDietId(diet.getDietId());
            dietRepository.insertDietItem(item);
        }

        return getDietDetail(userId, diet.getDietId());
    }

    @Transactional(readOnly = true)
    public DietDetailResponse getDietDetail(Long userId, Long dietId) {
        Diet diet = findDiet(userId, dietId);
        return toDetailResponse(diet);
    }

    @Transactional
    public DietDetailResponse updateDiet(Long userId, Long dietId, DietUpdateRequest request) {
        validateItems(request.items());
        Diet diet = findDiet(userId, dietId);
        LocalDate newDate = request.eatenAt().toLocalDate();
        assertNotDuplicated(userId, newDate, request.mealType(), dietId);

        List<DietItem> items = calculateItems(request.items());
        diet.setMealType(request.mealType());
        diet.setEatenAt(request.eatenAt());
        diet.setMemo(request.memo());
        applyTotals(diet, items);

        dietRepository.updateDiet(diet);
        dietRepository.deleteItemsByDietId(dietId);
        for (DietItem item : items) {
            item.setDietId(dietId);
            dietRepository.insertDietItem(item);
        }
        dietRepository.updateDietTotals(diet);

        return getDietDetail(userId, dietId);
    }

    @Transactional
    public DietDeleteResponse deleteDiet(Long userId, Long dietId) {
        findDiet(userId, dietId);
        int deletedCount = dietRepository.deleteByIdAndUserId(dietId, userId);
        if (deletedCount == 0) {
            throw new BusinessException(DietErrorCode.DIET_NOT_FOUND);
        }
        return new DietDeleteResponse(dietId, true, LocalDateTime.now());
    }

    private Diet findDiet(Long userId, Long dietId) {
        return dietRepository.findByIdAndUserId(dietId, userId)
                .orElseThrow(() -> new BusinessException(DietErrorCode.DIET_NOT_FOUND));
    }

    private void validateItems(List<DietItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new BusinessException(DietErrorCode.INVALID_DIET_ITEMS);
        }
    }

    private void assertNotDuplicated(Long userId, LocalDate date, MealType mealType, Long currentDietId) {
        dietRepository.findByUserIdAndDateAndMealType(userId, startOf(date), endOf(date), mealType)
                .filter(diet -> currentDietId == null || !diet.getDietId().equals(currentDietId))
                .ifPresent(diet -> {
                    throw new BusinessException(DietErrorCode.DIET_ALREADY_EXISTS);
                });
    }

    private List<DietItem> calculateItems(List<DietItemRequest> requests) {
        return requests.stream()
                .map(request -> {
                    Food food = foodRepository.findById(request.foodId())
                            .orElseThrow(() -> new BusinessException(FoodErrorCode.FOOD_NOT_FOUND));
                    return nutritionCalculator.calculate(request, food);
                })
                .toList();
    }

    private void applyTotals(Diet diet, List<DietItem> items) {
        diet.setTotalCalories(sum(items, DietItem::getCalories));
        diet.setTotalProteinG(sum(items, DietItem::getProteinG));
        diet.setTotalCarbsG(sum(items, DietItem::getCarbsG));
        diet.setTotalFatG(sum(items, DietItem::getFatG));
        diet.setTotalSugarG(sum(items, DietItem::getSugarG));
        diet.setTotalSodiumMg(sum(items, DietItem::getSodiumMg));
        diet.setTotalFiberG(sum(items, DietItem::getFiberG));
        diet.setTotalIronMg(sum(items, DietItem::getIronMg));
        diet.setTotalPhosphorusMg(sum(items, DietItem::getPhosphorusMg));
        diet.setTotalPotassiumMg(sum(items, DietItem::getPotassiumMg));
        diet.setTotalVitaminAUgRae(sum(items, DietItem::getVitaminAUgRae));
        diet.setTotalBetaCaroteneUg(sum(items, DietItem::getBetaCaroteneUg));
        diet.setTotalRetinolUg(sum(items, DietItem::getRetinolUg));
    }

    private BigDecimal sum(List<DietItem> items, NutrientGetter getter) {
        return items.stream()
                .map(getter::get)
                .filter(value -> value != null)
                .reduce(ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private DietMealResponse emptyMeal(MealType mealType) {
        return new DietMealResponse(
                mealType,
                mealType.getLabel(),
                mealType.getTimeRange(),
                false,
                null,
                null,
                null,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                List.of()
        );
    }

    private DietMealResponse toMealResponse(Diet diet) {
        return new DietMealResponse(
                diet.getMealType(),
                diet.getMealType().getLabel(),
                diet.getMealType().getTimeRange(),
                true,
                diet.getDietId(),
                diet.getEatenAt(),
                diet.getMemo(),
                zeroIfNull(diet.getTotalCalories()),
                zeroIfNull(diet.getTotalProteinG()),
                zeroIfNull(diet.getTotalCarbsG()),
                zeroIfNull(diet.getTotalFatG()),
                dietRepository.findItemsByDietId(diet.getDietId()).stream()
                        .map(this::toItemResponse)
                        .toList()
        );
    }

    private DietDetailResponse toDetailResponse(Diet diet) {
        List<DietItemResponse> items = dietRepository.findItemsByDietId(diet.getDietId())
                .stream()
                .map(this::toItemResponse)
                .toList();

        return new DietDetailResponse(
                diet.getDietId(),
                diet.getMealType(),
                diet.getEatenAt(),
                diet.getMemo(),
                zeroIfNull(diet.getTotalCalories()),
                zeroIfNull(diet.getTotalProteinG()),
                zeroIfNull(diet.getTotalCarbsG()),
                zeroIfNull(diet.getTotalFatG()),
                zeroIfNull(diet.getTotalSugarG()),
                zeroIfNull(diet.getTotalSodiumMg()),
                items,
                diet.getCreatedAt(),
                diet.getUpdatedAt()
        );
    }

    private DietItemResponse toItemResponse(DietItemRow row) {
        return new DietItemResponse(
                row.getDietItemId(),
                row.getFoodId(),
                row.getFoodName(),
                row.getBrand(),
                row.getInputAmount(),
                row.getInputUnit(),
                row.getAmountG(),
                row.getAmountMl(),
                zeroIfNull(row.getCalories()),
                zeroIfNull(row.getProteinG()),
                zeroIfNull(row.getCarbsG()),
                zeroIfNull(row.getFatG()),
                zeroIfNull(row.getSugarG()),
                zeroIfNull(row.getSodiumMg())
        );
    }

    private DailyNutritionSummaryResponse buildDailySummary(Long userId, List<Diet> diets) {
        NutritionTargetRow target = dietRepository.findNutritionTargetByUserId(userId)
                .orElse(new NutritionTargetRow());
        BigDecimal totalCalories = sumDiets(diets, Diet::getTotalCalories);
        BigDecimal totalProtein = sumDiets(diets, Diet::getTotalProteinG);
        BigDecimal totalCarbs = sumDiets(diets, Diet::getTotalCarbsG);
        BigDecimal totalFat = sumDiets(diets, Diet::getTotalFatG);
        BigDecimal targetCalories = zeroIfNull(target.getTargetCalories());
        BigDecimal targetProtein = zeroIfNull(target.getTargetProteinG());
        BigDecimal targetCarbs = zeroIfNull(target.getTargetCarbsG());
        BigDecimal targetFat = zeroIfNull(target.getTargetFatG());

        return new DailyNutritionSummaryResponse(
                totalCalories,
                targetCalories,
                rate(totalCalories, targetCalories),
                totalProtein,
                targetProtein,
                rate(totalProtein, targetProtein),
                totalCarbs,
                targetCarbs,
                rate(totalCarbs, targetCarbs),
                totalFat,
                targetFat,
                rate(totalFat, targetFat),
                0,
                TARGET_VEGETABLE_SERVINGS,
                0
        );
    }

    private BigDecimal sumDiets(List<Diet> diets, DietNutrientGetter getter) {
        return diets.stream()
                .map(getter::get)
                .filter(value -> value != null)
                .reduce(ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private int rate(BigDecimal value, BigDecimal target) {
        if (target == null || target.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return value.multiply(BigDecimal.valueOf(100))
                .divide(target, 0, RoundingMode.HALF_UP)
                .intValue();
    }

    private LocalDateTime startOf(LocalDate date) {
        return date.atStartOfDay();
    }

    private LocalDateTime endOf(LocalDate date) {
        return date.plusDays(1).atStartOfDay();
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    @FunctionalInterface
    private interface NutrientGetter {
        BigDecimal get(DietItem item);
    }

    @FunctionalInterface
    private interface DietNutrientGetter {
        BigDecimal get(Diet diet);
    }
}
