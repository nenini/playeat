package com.nyamnyam.coach.food.repository;

import com.nyamnyam.coach.food.entity.Food;
import com.nyamnyam.coach.food.repository.row.FrequentFoodRow;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@Sql(scripts = "/test-auth-schema.sql")
class FoodRepositoryTest {

    private final FoodRepository foodRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    FoodRepositoryTest(FoodRepository foodRepository, JdbcTemplate jdbcTemplate) {
        this.foodRepository = foodRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    void searchByKeywordReturnsFoodsWithPagination() {
        insertFood("F-001", "김밥", "밥류", "100g", "230g", null);
        insertFood("F-002", "참치김밥", "밥류", "100g", "250g", null);
        insertFood("F-003", "국밥_돼지머리", "밥류", "100g", "900g", null);

        List<Food> foods = foodRepository.searchByKeyword("김밥", 10, 0);

        assertThat(foods).hasSize(2);
        assertThat(foods).extracting(Food::getName)
                .containsExactly("김밥", "참치김밥");
        assertThat(foods.get(0).getNutritionBasisAmount()).isEqualByComparingTo("100.00");
        assertThat(foods.get(0).getNutritionBasisUnit()).isEqualTo("g");
        assertThat(foods.get(0).getServingAmount()).isEqualByComparingTo("230.00");
        assertThat(foods.get(0).getServingUnit()).isEqualTo("g");
    }

    @Test
    void countByKeywordReturnsTotal() {
        insertFood("F-001", "김밥", "밥류", "100g", "230g", null);
        insertFood("F-002", "참치김밥", "밥류", "100g", "250g", null);
        insertFood("F-003", "국밥_돼지머리", "밥류", "100g", "900g", null);

        long count = foodRepository.countByKeyword("김밥");

        assertThat(count).isEqualTo(2);
    }

    @Test
    void searchByKeywordDeduplicatesByNameAndPrefersGenericFood() {
        String coffeeName = "커피_아메리카노 아이스(ICED)";
        insertFoodWithBrand("F-001", coffeeName, "메가커피", "음료 및 차류", "100ml", "710ml", null);
        Long genericFoodId = insertFoodWithBrand("F-002", coffeeName, null, "음료 및 차류", "100g", "355g", null);
        insertFoodWithBrand("F-003", coffeeName, "이디야", "음료 및 차류", "100ml", "414ml", null);
        insertFoodWithBrand("F-004", "커피_카페 라떼 아이스(ICED)", "이디야", "음료 및 차류", "100ml", "414ml", null);

        List<Food> foods = foodRepository.searchByKeyword("커피", 10, 0);
        long count = foodRepository.countByKeyword("커피");

        assertThat(foods).hasSize(2);
        assertThat(count).isEqualTo(2);
        assertThat(foods.get(0).getFoodId()).isEqualTo(genericFoodId);
        assertThat(foods.get(0).getName()).isEqualTo(coffeeName);
        assertThat(foods.get(0).getBrand()).isNull();
    }

    @Test
    void findByIdReturnsFood() {
        Long foodId = insertFood("F-001", "스무디_코코넛", "음료류", "100ml", "360ml", null);

        Optional<Food> food = foodRepository.findById(foodId);

        assertThat(food).isPresent();
        assertThat(food.get().getName()).isEqualTo("스무디_코코넛");
        assertThat(food.get().isLiquidBasis()).isTrue();
        assertThat(food.get().getNutritionBasisUnit()).isEqualTo("ml");
        assertThat(food.get().getServingUnit()).isEqualTo("ml");
    }

    @Test
    void findByIdReturnsEmptyForMissingFood() {
        Optional<Food> food = foodRepository.findById(999L);

        assertThat(food).isEmpty();
    }

    @Test
    void findByIdReturnsGramPerPieceForClearPieceFood() {
        Long foodId = insertFood("F-001", "도넛_달콤한꿀도넛 (3개입)", "빵 및 과자류", "100g", "105g", new BigDecimal("35"));

        Food food = foodRepository.findById(foodId).orElseThrow();

        assertThat(food.hasPieceAmount()).isTrue();
        assertThat(food.getGramPerPiece()).isEqualByComparingTo("35.0000");
    }

    @Test
    void findFrequentFoodsAggregatesByUserAndOrdersByCountThenLastRecordedAt() {
        Long userId = insertUser("user@example.com");
        Long otherUserId = insertUser("other@example.com");
        Long kimBapId = insertFood("F-001", "김밥", "밥류", "100g", "230g", null);
        Long smoothieId = insertFood("F-002", "스무디_코코넛", "음료류", "100ml", "360ml", null);
        Long soupId = insertFood("F-003", "국밥_돼지머리", "밥류", "100g", "900g", null);

        Long breakfast = insertDiet(userId, "BREAKFAST", LocalDateTime.of(2026, 5, 26, 8, 0));
        Long lunch = insertDiet(userId, "LUNCH", LocalDateTime.of(2026, 5, 26, 12, 30));
        Long dinner = insertDiet(userId, "DINNER", LocalDateTime.of(2026, 5, 27, 19, 0));
        Long otherDiet = insertDiet(otherUserId, "LUNCH", LocalDateTime.of(2026, 5, 28, 12, 0));

        insertDietItem(breakfast, kimBapId, new BigDecimal("230"), "g");
        insertDietItem(lunch, kimBapId, new BigDecimal("230"), "g");
        insertDietItem(dinner, smoothieId, new BigDecimal("360"), "ml");
        insertDietItem(lunch, soupId, new BigDecimal("900"), "g");
        insertDietItem(dinner, soupId, new BigDecimal("900"), "g");
        insertDietItem(otherDiet, kimBapId, new BigDecimal("230"), "g");

        List<FrequentFoodRow> foods = foodRepository.findFrequentFoods(userId, 10);

        assertThat(foods).hasSize(3);
        assertThat(foods).extracting(FrequentFoodRow::getName)
                .containsExactly("국밥_돼지머리", "김밥", "스무디_코코넛");
        assertThat(foods.get(0).getRecordCount()).isEqualTo(2L);
        assertThat(foods.get(0).getLastRecordedAt()).isEqualTo(LocalDateTime.of(2026, 5, 27, 19, 0));
        assertThat(foods.get(1).getRecordCount()).isEqualTo(2L);
        assertThat(foods.get(2).getRecordCount()).isEqualTo(1L);
    }

    private Long insertFood(
            String externalFoodCode,
            String name,
            String category,
            String nutritionBasis,
            String servingSize,
            BigDecimal gramPerPiece
    ) {
        return insertFoodWithBrand(
                externalFoodCode,
                name,
                "테스트브랜드",
                category,
                nutritionBasis,
                servingSize,
                gramPerPiece
        );
    }

    private Long insertFoodWithBrand(
            String externalFoodCode,
            String name,
            String brand,
            String category,
            String nutritionBasis,
            String servingSize,
            BigDecimal gramPerPiece
    ) {
        ParsedAmountUnit basis = parseAmountUnit(nutritionBasis);
        ParsedAmountUnit serving = parseAmountUnit(servingSize);
        jdbcTemplate.update(
                """
                INSERT INTO foods (
                    external_food_code,
                    name,
                    brand,
                    category,
                    nutrition_basis_amount,
                    nutrition_basis_unit,
                    serving_amount,
                    serving_unit,
                    gram_per_piece,
                    calories,
                    protein_g,
                    carbs_g,
                    fat_g,
                    sugar_g,
                    sodium_mg,
                    fiber_g,
                    iron_mg,
                    phosphorus_mg,
                    potassium_mg,
                    vitamin_a_ug_rae,
                    beta_carotene_ug,
                    retinol_ug,
                    source
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 135, 7.17, 5.36, 9.49, 3.25, 88, 0, 0, 0, 0, 0, 0, 0, '테스트출처')
                """,
                externalFoodCode,
                name,
                brand,
                category,
                basis.amount(),
                basis.unit(),
                serving.amount(),
                serving.unit(),
                gramPerPiece
        );
        return jdbcTemplate.queryForObject("SELECT food_id FROM foods WHERE external_food_code = ?", Long.class, externalFoodCode);
    }

    private Long insertUser(String email) {
        jdbcTemplate.update(
                """
                INSERT INTO users (
                    email,
                    password_hash,
                    nickname,
                    status,
                    onboarding_completed
                )
                VALUES (?, 'encoded-password', ?, 'ACTIVE', TRUE)
                """,
                email,
                email.substring(0, email.indexOf('@'))
        );
        return jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = ?", Long.class, email);
    }

    private Long insertDiet(Long userId, String mealType, LocalDateTime eatenAt) {
        jdbcTemplate.update(
                """
                INSERT INTO diets (
                    user_id,
                    meal_type,
                    eaten_at
                )
                VALUES (?, ?, ?)
                """,
                userId,
                mealType,
                eatenAt
        );
        return jdbcTemplate.queryForObject(
                "SELECT diet_id FROM diets WHERE user_id = ? AND meal_type = ? AND eaten_at = ?",
                Long.class,
                userId,
                mealType,
                eatenAt
        );
    }

    private void insertDietItem(Long dietId, Long foodId, BigDecimal amount, String unit) {
        jdbcTemplate.update(
                """
                INSERT INTO diet_items (
                    diet_id,
                    food_id,
                    input_amount,
                    input_unit,
                    amount_g,
                    amount_ml
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                dietId,
                foodId,
                amount,
                unit,
                "g".equals(unit) ? amount : null,
                "ml".equals(unit) ? amount : null
        );
    }

    private ParsedAmountUnit parseAmountUnit(String raw) {
        String amountText = raw.replaceAll("[^0-9.]", "");
        String unitText = raw.replaceAll("[0-9.]", "");
        return new ParsedAmountUnit(new BigDecimal(amountText), unitText);
    }

    private record ParsedAmountUnit(BigDecimal amount, String unit) {
    }
}
