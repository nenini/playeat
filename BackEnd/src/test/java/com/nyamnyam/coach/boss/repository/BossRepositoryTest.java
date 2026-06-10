package com.nyamnyam.coach.boss.repository;

import com.nyamnyam.coach.boss.repository.row.BossCommonConditionRow;
import com.nyamnyam.coach.boss.repository.row.BossRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@Sql(scripts = "/test-auth-schema.sql")
class BossRepositoryTest {

    private final BossRepository bossRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    BossRepositoryTest(BossRepository bossRepository, JdbcTemplate jdbcTemplate) {
        this.bossRepository = bossRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    @DisplayName("현재 시즌 보스가 EASY, NORMAL, HARD 순서로 조회된다")
    void findCurrentBosses() {
        Long seasonId = insertSeason();
        insertBoss(seasonId, "HARD", "폭식 드래곤", 7000, "ACTIVE");
        insertBoss(seasonId, "EASY", "설탕 슬라임", 1000, "ACTIVE");
        insertBoss(seasonId, "NORMAL", "야식 골렘", 3000, "ACTIVE");

        List<BossRow> bosses = bossRepository.findCurrentBosses();

        assertThat(bosses).hasSize(3);
        assertThat(bosses).extracting(BossRow::getDifficulty).containsExactly("EASY", "NORMAL", "HARD");
    }

    @Test
    @DisplayName("현재 시즌 공통 격파 조건을 sortOrder 순서로 조회한다")
    void findCommonConditionsBySeasonId() {
        Long seasonId = insertSeason();
        insertCondition(seasonId, "두 번째 조건", 2);
        insertCondition(seasonId, "첫 번째 조건", 1);

        List<BossCommonConditionRow> conditions = bossRepository.findCommonConditionsBySeasonId(seasonId);

        assertThat(conditions).hasSize(2);
        assertThat(conditions).extracting(BossCommonConditionRow::getTitle)
                .containsExactly("첫 번째 조건", "두 번째 조건");
    }

    @Test
    @DisplayName("bossId로 보스 상세를 조회하고 ACTIVE 여부를 확인한다")
    void findBossById() {
        Long seasonId = insertSeason();
        Long bossId = insertBoss(seasonId, "EASY", "설탕 슬라임", 1000, "ACTIVE");

        Optional<BossRow> boss = bossRepository.findBossById(bossId);

        assertThat(boss).isPresent();
        assertThat(boss.get().getName()).isEqualTo("설탕 슬라임");
        assertThat(bossRepository.existsActiveBossById(bossId)).isTrue();
    }

    @Test
    @DisplayName("INACTIVE 보스는 active 존재 확인에서 제외된다")
    void existsActiveBossByIdInactive() {
        Long seasonId = insertSeason();
        Long bossId = insertBoss(seasonId, "EASY", "잠든 보스", 1000, "INACTIVE");

        assertThat(bossRepository.existsActiveBossById(bossId)).isFalse();
    }

    private Long insertSeason() {
        jdbcTemplate.update(
                """
                INSERT INTO boss_seasons (
                    season_code,
                    name,
                    start_date,
                    end_date,
                    active
                )
                VALUES ('S-2026-W24', '2026년 6월 2주차', DATEADD('DAY', -1, CURRENT_DATE), DATEADD('DAY', 6, CURRENT_DATE), TRUE)
                """
        );
        return jdbcTemplate.queryForObject("SELECT season_id FROM boss_seasons WHERE season_code = 'S-2026-W24'", Long.class);
    }

    private Long insertBoss(Long seasonId, String difficulty, String name, int maxHp, String status) {
        jdbcTemplate.update(
                """
                INSERT INTO bosses (
                    season_id,
                    name,
                    description,
                    difficulty,
                    max_hp,
                    image_url,
                    reward_exp,
                    reward_coin,
                    status,
                    starts_at,
                    ends_at
                )
                VALUES (?, ?, ?, ?, ?, 'https://example.com/boss.png', 100, 50, ?, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', 6, CURRENT_TIMESTAMP))
                """,
                seasonId,
                name,
                name + " 설명",
                difficulty,
                maxHp,
                status
        );
        return jdbcTemplate.queryForObject("SELECT boss_id FROM bosses WHERE season_id = ? AND difficulty = ?", Long.class, seasonId, difficulty);
    }

    private void insertCondition(Long seasonId, String title, int sortOrder) {
        jdbcTemplate.update(
                """
                INSERT INTO boss_common_conditions (
                    season_id,
                    title,
                    description,
                    target_type,
                    target_value,
                    unit,
                    sort_order
                )
                VALUES (?, ?, '조건 설명', 'DIET_RECORD_MEMBER_COUNT', 4, '명', ?)
                """,
                seasonId,
                title,
                sortOrder
        );
    }
}
