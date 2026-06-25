package com.nyamnyam.coach.food.service;

import com.nyamnyam.coach.food.dto.response.FoodDetailResponse;
import com.nyamnyam.coach.food.dto.response.FoodSearchPageResponse;
import com.nyamnyam.coach.food.dto.response.FrequentFoodListResponse;
import com.nyamnyam.coach.food.entity.Food;
import com.nyamnyam.coach.food.repository.FoodRepository;
import com.nyamnyam.coach.food.repository.row.FrequentFoodRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.FoodErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    private FoodService foodService;

    @BeforeEach
    void setUp() {
        foodService = new FoodService(foodRepository);
    }

    @Test
    @DisplayName("검색어를 trim하고 page, size로 offset을 계산해 음식 목록을 조회한다")
    void searchFoods() {
        Food egg = food(1L, "삶은 계란");
        when(foodRepository.searchByKeyword("계란", 10, 20)).thenReturn(List.of(egg));
        when(foodRepository.countByKeyword("계란")).thenReturn(21L);

        FoodSearchPageResponse response = foodService.searchFoods("  계란  ", 2, 10);

        assertThat(response.foods()).hasSize(1);
        assertThat(response.foods().get(0).foodId()).isEqualTo(1L);
        assertThat(response.foods().get(0).protein()).isEqualByComparingTo("6.50");
        assertThat(response.page()).isEqualTo(2);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.totalElements()).isEqualTo(21L);
        assertThat(response.totalPages()).isEqualTo(3);
        verify(foodRepository).searchByKeyword("계란", 10, 20);
        verify(foodRepository).countByKeyword("계란");
    }

    @Test
    @DisplayName("검색어가 비어 있으면 INVALID_KEYWORD 예외를 던진다")
    void searchFoodsWithBlankKeyword() {
        assertThatThrownBy(() -> foodService.searchFoods("   ", 0, 20))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(FoodErrorCode.INVALID_KEYWORD);

        verifyNoInteractions(foodRepository);
    }

    @Test
    @DisplayName("검색 page와 size가 범위를 벗어나면 기본 범위로 보정한다")
    void searchFoodsNormalizePageAndSize() {
        when(foodRepository.searchByKeyword("닭가슴살", 100, 0)).thenReturn(List.of());
        when(foodRepository.countByKeyword("닭가슴살")).thenReturn(0L);

        FoodSearchPageResponse response = foodService.searchFoods("닭가슴살", -1, 200);

        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(100);
        assertThat(response.totalPages()).isZero();
        verify(foodRepository).searchByKeyword("닭가슴살", 100, 0);
    }

    @Test
    @DisplayName("음식 상세 정보를 조회한다")
    void getFoodDetail() {
        Food smoothie = food(2L, "스무디_코코넛");
        when(foodRepository.findById(2L)).thenReturn(Optional.of(smoothie));

        FoodDetailResponse response = foodService.getFoodDetail(2L);

        assertThat(response.foodId()).isEqualTo(2L);
        assertThat(response.name()).isEqualTo("스무디_코코넛");
        assertThat(response.nutritionBasisUnit()).isEqualTo("g");
        assertThat(response.calories()).isEqualByComparingTo("78.00");
        assertThat(response.fiber()).isEqualByComparingTo("1.20");
        verify(foodRepository).findById(2L);
    }

    @Test
    @DisplayName("음식 상세 정보가 없으면 FOOD_NOT_FOUND 예외를 던진다")
    void getFoodDetailNotFound() {
        when(foodRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodService.getFoodDetail(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(FoodErrorCode.FOOD_NOT_FOUND);
    }

    @Test
    @DisplayName("자주 먹은 음식 목록을 조회하고 limit을 보정한다")
    void getFrequentFoods() {
        LocalDateTime lastRecordedAt = LocalDateTime.of(2026, 6, 12, 19, 30);
        FrequentFoodRow row = FrequentFoodRow.builder()
                .foodId(1L)
                .name("삶은 계란")
                .recordCount(3L)
                .lastRecordedAt(lastRecordedAt)
                .build();
        when(foodRepository.findFrequentFoods(10L, 50)).thenReturn(List.of(row));

        FrequentFoodListResponse response = foodService.getFrequentFoods(10L, 100);

        assertThat(response.foods()).hasSize(1);
        assertThat(response.foods().get(0).foodId()).isEqualTo(1L);
        assertThat(response.foods().get(0).recordCount()).isEqualTo(3L);
        assertThat(response.foods().get(0).lastRecordedAt()).isEqualTo(lastRecordedAt);
        verify(foodRepository).findFrequentFoods(10L, 50);
    }

    private Food food(Long foodId, String name) {
        return Food.builder()
                .foodId(foodId)
                .externalFoodCode("EXT-" + foodId)
                .name(name)
                .brand("테스트브랜드")
                .category("테스트분류")
                .nutritionBasisAmount(new BigDecimal("100.00"))
                .nutritionBasisUnit("g")
                .servingAmount(new BigDecimal("60.00"))
                .servingUnit("g")
                .gramPerPiece(new BigDecimal("60.0000"))
                .calories(new BigDecimal("78.00"))
                .proteinG(new BigDecimal("6.50"))
                .carbsG(new BigDecimal("0.50"))
                .fatG(new BigDecimal("5.30"))
                .sugarG(new BigDecimal("0.20"))
                .sodiumMg(new BigDecimal("62.00"))
                .fiberG(new BigDecimal("1.20"))
                .ironMg(new BigDecimal("1.10"))
                .phosphorusMg(new BigDecimal("95.00"))
                .potassiumMg(new BigDecimal("75.00"))
                .vitaminAUgRae(new BigDecimal("80.00"))
                .betaCaroteneUg(new BigDecimal("15.00"))
                .retinolUg(new BigDecimal("70.00"))
                .source("TEST")
                .build();
    }
}
