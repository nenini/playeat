package com.nyamnyam.coach.quest.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@Sql(scripts = "/test-auth-schema.sql")
class QuestConditionRepositoryTest {

    private static final long BATTLE_ID = 101L;
    private static final long GUILD_ID = 201L;
    private static final long USER_ID = 301L;
    private static final LocalDateTime START_AT = LocalDateTime.of(2026, 6, 15, 0, 0);
    private static final LocalDateTime END_AT = LocalDateTime.of(2026, 6, 22, 0, 0);

    private final QuestRepository questRepository;
    private final JdbcTemplate jdbcTemplate;
    private final SqlSession sqlSession;

    @Autowired
    QuestConditionRepositoryTest(
            QuestRepository questRepository,
            JdbcTemplate jdbcTemplate,
            SqlSession sqlSession
    ) {
        this.questRepository = questRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.sqlSession = sqlSession;
    }

    @Test
    @DisplayName("TOTAL_COUNT는 같은 날 조건을 만족한 식단 행을 각각 집계한다")
    void countSatisfiedDietRowsWithoutDailyGrouping() {
        insertParticipant();
        insertDiet(START_AT.plusHours(8), "10", "10", "900");
        assertThat(count("SUGAR", "LESS_THAN_OR_EQUAL", "20")).isEqualTo(1);

        insertDiet(START_AT.plusHours(12), "15", "10", "900");
        assertThat(count("SUGAR", "LESS_THAN_OR_EQUAL", "20")).isEqualTo(2);

        insertDiet(START_AT.plusHours(18), "18", "10", "900");
        assertThat(count("SUGAR", "LESS_THAN_OR_EQUAL", "20")).isEqualTo(3);

        insertDiet(START_AT.plusHours(20), "25", "10", "900");
        insertDiet(START_AT.minusMinutes(1), "5", "10", "900");
        insertDiet(END_AT, "5", "10", "900");
        assertThat(count("SUGAR", "LESS_THAN_OR_EQUAL", "20")).isEqualTo(3);

        jdbcTemplate.update(
                "DELETE FROM diets WHERE user_id = ? AND eaten_at = ?",
                USER_ID,
                START_AT.plusHours(18)
        );
        sqlSession.clearCache();

        assertThat(count("SUGAR", "LESS_THAN_OR_EQUAL", "20")).isEqualTo(2);
    }

    @Test
    @DisplayName("TOTAL_COUNT는 단백질 이상 조건과 나트륨 이하 조건을 식단별로 평가한다")
    void countProteinAndSodiumConditionsPerDiet() {
        insertParticipant();
        insertDiet(START_AT.plusHours(8), "10", "25", "800");
        insertDiet(START_AT.plusHours(12), "10", "24.99", "801");
        insertDiet(START_AT.plusHours(18), "10", "40", "500");

        assertThat(count("PROTEIN", "GREATER_THAN_OR_EQUAL", "25")).isEqualTo(2);
        assertThat(count("SODIUM", "LESS_THAN_OR_EQUAL", "800")).isEqualTo(2);
    }

    private int count(String metricType, String comparisonType, String threshold) {
        return questRepository.countGuildBattleSatisfiedDiets(
                BATTLE_ID,
                START_AT,
                END_AT,
                metricType,
                comparisonType,
                new BigDecimal(threshold),
                null,
                null
        );
    }

    private void insertParticipant() {
        jdbcTemplate.update(
                """
                        INSERT INTO boss_battle_participants (
                            battle_id, guild_id, user_id, role_at_start, status, joined_at
                        ) VALUES (?, ?, ?, 'MEMBER', 'ACTIVE', ?)
                        """,
                BATTLE_ID,
                GUILD_ID,
                USER_ID,
                START_AT
        );
        sqlSession.clearCache();
    }

    private void insertDiet(
            LocalDateTime eatenAt,
            String sugar,
            String protein,
            String sodium
    ) {
        jdbcTemplate.update(
                """
                        INSERT INTO diets (
                            user_id, meal_type, eaten_at,
                            total_sugar_g, total_protein_g, total_sodium_mg
                        ) VALUES (?, 'MEAL', ?, ?, ?, ?)
                        """,
                USER_ID,
                eatenAt,
                new BigDecimal(sugar),
                new BigDecimal(protein),
                new BigDecimal(sodium)
        );
        sqlSession.clearCache();
    }
}
