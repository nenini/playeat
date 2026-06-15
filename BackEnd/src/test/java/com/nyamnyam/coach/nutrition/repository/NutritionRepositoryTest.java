package com.nyamnyam.coach.nutrition.repository;

import com.nyamnyam.coach.nutrition.repository.row.DailyNutritionAggregateRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@Sql(scripts = "/test-auth-schema.sql")
class NutritionRepositoryTest {

    private final NutritionRepository nutritionRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    NutritionRepositoryTest(
            NutritionRepository nutritionRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.nutritionRepository = nutritionRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    @DisplayName("건강 프로필이 없어도 지정 날짜 식단 섭취량을 집계한다")
    void findDailyAggregateWithoutHealthProfile() {
        Long userId = insertUser();
        insertDiet(userId, LocalDateTime.of(2026, 5, 14, 8, 10), "BREAKFAST", 450, 20, 60, 12, 700, 5);
        insertDiet(userId, LocalDateTime.of(2026, 5, 14, 19, 30), "DINNER", 650, 35, 80, 18, 900, 8);
        insertDiet(userId, LocalDateTime.of(2026, 5, 15, 8, 10), "BREAKFAST", 300, 10, 40, 8, 500, 3);

        DailyNutritionAggregateRow row = nutritionRepository.findDailyAggregate(
                userId,
                LocalDate.of(2026, 5, 14).atStartOfDay(),
                LocalDate.of(2026, 5, 15).atStartOfDay()
        );

        assertThat(row).isNotNull();
        assertThat(row.getTotalCalories()).isEqualByComparingTo("1100.00");
        assertThat(row.getTotalProteinG()).isEqualByComparingTo("55.00");
        assertThat(row.getTotalCarbsG()).isEqualByComparingTo("140.00");
        assertThat(row.getTotalFatG()).isEqualByComparingTo("30.00");
        assertThat(row.getTotalSodiumMg()).isEqualByComparingTo("1600.00");
        assertThat(row.getTotalFiberG()).isEqualByComparingTo("13.00");
        assertThat(row.getTargetCalories()).isNull();
    }

    private Long insertUser() {
        jdbcTemplate.update(
                """
                INSERT INTO users (
                    email,
                    password_hash,
                    nickname,
                    status
                )
                VALUES ('nutrition@example.com', 'encoded-password', '영양이', 'ACTIVE')
                """
        );
        return jdbcTemplate.queryForObject(
                "SELECT user_id FROM users WHERE email = 'nutrition@example.com'",
                Long.class
        );
    }

    private void insertDiet(
            Long userId,
            LocalDateTime eatenAt,
            String mealType,
            int calories,
            int proteinG,
            int carbsG,
            int fatG,
            int sodiumMg,
            int fiberG
    ) {
        jdbcTemplate.update(
                """
                INSERT INTO diets (
                    user_id,
                    meal_type,
                    eaten_at,
                    total_calories,
                    total_protein_g,
                    total_carbs_g,
                    total_fat_g,
                    total_sodium_mg,
                    total_fiber_g
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                userId,
                mealType,
                eatenAt,
                BigDecimal.valueOf(calories),
                BigDecimal.valueOf(proteinG),
                BigDecimal.valueOf(carbsG),
                BigDecimal.valueOf(fatG),
                BigDecimal.valueOf(sodiumMg),
                BigDecimal.valueOf(fiberG)
        );
    }
}
