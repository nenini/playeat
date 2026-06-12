package com.nyamnyam.coach.food.service;

import com.nyamnyam.coach.food.dto.response.FoodDetailResponse;
import com.nyamnyam.coach.food.dto.response.FoodSearchPageResponse;
import com.nyamnyam.coach.food.dto.response.FoodSearchResponse;
import com.nyamnyam.coach.food.dto.response.FrequentFoodListResponse;
import com.nyamnyam.coach.food.dto.response.FrequentFoodResponse;
import com.nyamnyam.coach.food.entity.Food;
import com.nyamnyam.coach.food.repository.FoodRepository;
import com.nyamnyam.coach.food.repository.row.FrequentFoodRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.FoodErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FoodService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_FREQUENT_LIMIT = 10;
    private static final int MIN_FREQUENT_LIMIT = 1;
    private static final int MAX_FREQUENT_LIMIT = 50;

    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    @Transactional(readOnly = true)
    public FoodSearchPageResponse searchFoods(String keyword, Integer page, Integer size) {
        String normalizedKeyword = normalizeKeyword(keyword);
        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        int offset = normalizedPage * normalizedSize;

        List<FoodSearchResponse> foods = foodRepository
                .searchByKeyword(normalizedKeyword, normalizedSize, offset)
                .stream()
                .map(this::toSearchResponse)
                .toList();
        long totalElements = foodRepository.countByKeyword(normalizedKeyword);

        return new FoodSearchPageResponse(
                foods,
                normalizedPage,
                normalizedSize,
                totalElements,
                calculateTotalPages(totalElements, normalizedSize)
        );
    }

    @Transactional(readOnly = true)
    public FoodDetailResponse getFoodDetail(Long foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new BusinessException(FoodErrorCode.FOOD_NOT_FOUND));
        return toDetailResponse(food);
    }

    @Transactional(readOnly = true)
    public FrequentFoodListResponse getFrequentFoods(Long userId, Integer limit) {
        int normalizedLimit = normalizeFrequentLimit(limit);
        List<FrequentFoodResponse> foods = foodRepository
                .findFrequentFoods(userId, normalizedLimit)
                .stream()
                .map(this::toFrequentFoodResponse)
                .toList();
        return new FrequentFoodListResponse(foods);
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessException(FoodErrorCode.INVALID_KEYWORD);
        }
        return escapeLikeWildcards(keyword.trim());
    }

    private String escapeLikeWildcards(String value) {
        return value.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private int normalizePage(Integer page) {
        if (page == null || page < DEFAULT_PAGE) {
            return DEFAULT_PAGE;
        }
        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null) {
            return DEFAULT_SIZE;
        }
        return Math.min(Math.max(size, MIN_SIZE), MAX_SIZE);
    }

    private int normalizeFrequentLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_FREQUENT_LIMIT;
        }
        return Math.min(Math.max(limit, MIN_FREQUENT_LIMIT), MAX_FREQUENT_LIMIT);
    }

    private int calculateTotalPages(long totalElements, int size) {
        if (totalElements == 0) {
            return 0;
        }
        return (int) ((totalElements + size - 1) / size);
    }

    private FoodSearchResponse toSearchResponse(Food food) {
        return new FoodSearchResponse(
                food.getFoodId(),
                food.getName(),
                food.getBrand(),
                food.getCategory(),
                food.getNutritionBasisAmount(),
                food.getNutritionBasisUnit(),
                food.getServingAmount(),
                food.getServingUnit(),
                food.getGramPerPiece(),
                food.getCalories(),
                food.getProteinG(),
                food.getCarbsG(),
                food.getFatG(),
                food.getSugarG(),
                food.getSodiumMg()
        );
    }

    private FoodDetailResponse toDetailResponse(Food food) {
        return new FoodDetailResponse(
                food.getFoodId(),
                food.getExternalFoodCode(),
                food.getName(),
                food.getBrand(),
                food.getCategory(),
                food.getNutritionBasisAmount(),
                food.getNutritionBasisUnit(),
                food.getServingAmount(),
                food.getServingUnit(),
                food.getGramPerPiece(),
                food.getCalories(),
                food.getProteinG(),
                food.getCarbsG(),
                food.getFatG(),
                food.getSugarG(),
                food.getSodiumMg(),
                food.getFiberG(),
                food.getIronMg(),
                food.getPhosphorusMg(),
                food.getPotassiumMg(),
                food.getVitaminAUgRae(),
                food.getBetaCaroteneUg(),
                food.getRetinolUg(),
                food.getSource()
        );
    }

    private FrequentFoodResponse toFrequentFoodResponse(FrequentFoodRow row) {
        return new FrequentFoodResponse(
                row.getFoodId(),
                row.getName(),
                row.getRecordCount(),
                row.getLastRecordedAt()
        );
    }
}
