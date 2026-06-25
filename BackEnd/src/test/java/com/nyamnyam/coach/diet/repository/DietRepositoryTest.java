package com.nyamnyam.coach.diet.repository;

import com.nyamnyam.coach.diet.entity.Diet;
import com.nyamnyam.coach.diet.entity.DietItem;
import com.nyamnyam.coach.diet.entity.MealType;
import com.nyamnyam.coach.diet.repository.row.DietItemRow;
import com.nyamnyam.coach.diet.repository.row.NutritionTargetRow;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@TestPropertySource(properties = "mybatis.mapper-locations=classpath:mappers/diet/*.xml,classpath:mappers/food/*.xml")
@Sql(scripts = "/test-auth-schema.sql")
class DietRepositoryTest {

    private final DietRepository dietRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    DietRepositoryTest(DietRepository dietRepository, JdbcTemplate jdbcTemplate) {
        this.dietRepository = dietRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    void insertDietAndFindByUserIdAndDate() {
        Long userId = insertUser("user@example.com");
        Diet diet = Diet.builder()
                .userId(userId)
                .mealType(MealType.BREAKFAST)
                .eatenAt(LocalDateTime.of(2026, 5, 15, 8, 10))
                .totalCalories(new BigDecimal("304.00"))
                .totalProteinG(new BigDecimal("10.40"))
                .totalCarbsG(new BigDecimal("52.80"))
                .totalFatG(new BigDecimal("5.60"))
                .totalSugarG(new BigDecimal("0.20"))
                .totalSodiumMg(new BigDecimal("62.00"))
                .build();

        dietRepository.insertDiet(diet);

        List<Diet> diets = dietRepository.findByUserIdAndDate(
                userId,
                LocalDateTime.of(2026, 5, 15, 0, 0),
                LocalDateTime.of(2026, 5, 16, 0, 0)
        );

        assertThat(diet.getDietId()).isNotNull();
        assertThat(diets).hasSize(1);
        assertThat(diets.get(0).getMealType()).isEqualTo(MealType.BREAKFAST);
        assertThat(diets.get(0).getTotalCalories()).isEqualByComparingTo("304.00");
    }

    @Test
    void findItemsByDietIdJoinsFoodName() {
        Long userId = insertUser("user@example.com");
        Long foodId = insertFood();
        Long dietId = insertDiet(userId);
        DietItem item = DietItem.builder()
                .dietId(dietId)
                .foodId(foodId)
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

        dietRepository.insertDietItem(item);

        List<DietItemRow> items = dietRepository.findItemsByDietId(dietId);

        assertThat(item.getDietItemId()).isNotNull();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getFoodName()).isEqualTo("오트밀");
        assertThat(items.get(0).getCalories()).isEqualByComparingTo("304.00");
    }

    @Test
    void findNutritionTargetByUserId() {
        Long userId = insertUser("user@example.com");
        jdbcTemplate.update(
                """
                INSERT INTO health_profiles (
                    user_id,
                    target_calories,
                    target_protein_g,
                    target_carbs_g,
                    target_fat_g,
                    target_fiber_g
                )
                VALUES (?, 2000, 90, 280, 65, 25)
                """,
                userId
        );

        Optional<NutritionTargetRow> target = dietRepository.findNutritionTargetByUserId(userId);

        assertThat(target).isPresent();
        assertThat(target.get().getTargetCalories()).isEqualByComparingTo("2000.00");
        assertThat(target.get().getTargetFiberG()).isEqualByComparingTo("25.00");
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

    private Long insertFood() {
        jdbcTemplate.update(
                """
                INSERT INTO foods (
                    external_food_code,
                    name,
                    nutrition_basis_amount,
                    nutrition_basis_unit,
                    calories,
                    protein_g,
                    carbs_g,
                    fat_g,
                    sugar_g,
                    sodium_mg
                )
                VALUES ('F-001', '오트밀', 100, 'g', 380, 13, 66, 7, 0.25, 77.5)
                """
        );
        return jdbcTemplate.queryForObject("SELECT food_id FROM foods WHERE external_food_code = 'F-001'", Long.class);
    }

    private Long insertDiet(Long userId) {
        jdbcTemplate.update(
                """
                INSERT INTO diets (
                    user_id,
                    meal_type,
                    eaten_at,
                    total_calories,
                    total_protein_g,
                    total_carbs_g,
                    total_fat_g
                )
                VALUES (?, 'BREAKFAST', '2026-05-15 08:10:00', 304, 10.4, 52.8, 5.6)
                """,
                userId
        );
        return jdbcTemplate.queryForObject(
                "SELECT diet_id FROM diets WHERE user_id = ? AND meal_type = 'BREAKFAST'",
                Long.class,
                userId
        );
    }
}
