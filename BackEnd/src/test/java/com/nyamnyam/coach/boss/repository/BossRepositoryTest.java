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
    @DisplayName("보스별 공통 격파 조건을 sortOrder 순서로 조회한다")
    void findCommonConditionsByBossId() {
        Long seasonId = insertSeason();
        Long easyBossId = insertBoss(seasonId, "EASY", "당분 드래곤", 50, "ACTIVE");
        Long hardBossId = insertBoss(seasonId, "HARD", "당분 드래곤", 200, "ACTIVE");
        insertCondition(seasonId, easyBossId, "EASY 조건", "SUGAR_UNDER_LIMIT", 1);
        insertCondition(seasonId, hardBossId, "HARD 두 번째 조건", "PROCESSED_DRINK_ZERO", 2);
        insertCondition(seasonId, hardBossId, "HARD 첫 번째 조건", "SUGAR_UNDER_LIMIT", 1);

        List<BossCommonConditionRow> conditions = bossRepository.findCommonConditionsByBossId(hardBossId);

        assertThat(conditions).hasSize(2);
        assertThat(conditions).extracting(BossCommonConditionRow::getTitle)
                .containsExactly("HARD 첫 번째 조건", "HARD 두 번째 조건");
        assertThat(conditions).extracting(BossCommonConditionRow::getBossId)
                .containsOnly(hardBossId);
    }

    @Test
    @DisplayName("같은 seasonId라도 다른 난이도의 조건이 섞이지 않는다")
    void findCommonConditionsByBossIdDoesNotMixDifficulty() {
        Long seasonId = insertSeason();
        Long easyBossId = insertBoss(seasonId, "EASY", "당분 드래곤", 50, "ACTIVE");
        Long normalBossId = insertBoss(seasonId, "NORMAL", "당분 드래곤", 100, "ACTIVE");
        Long hardBossId = insertBoss(seasonId, "HARD", "당분 드래곤", 200, "ACTIVE");
        insertCondition(seasonId, easyBossId, "당류 50g 이하 유지", "SUGAR_UNDER_LIMIT", 1);
        insertCondition(seasonId, normalBossId, "당류 50g 이하 유지", "SUGAR_UNDER_LIMIT", 1);
        insertCondition(seasonId, normalBossId, "가공음료 0회", "PROCESSED_DRINK_ZERO", 2);
        insertCondition(seasonId, hardBossId, "당류 50g 이하 유지", "SUGAR_UNDER_LIMIT", 1);
        insertCondition(seasonId, hardBossId, "가공음료 0회", "PROCESSED_DRINK_ZERO", 2);
        insertCondition(seasonId, hardBossId, "채소 하루 2종 이상", "VEGETABLE_VARIETY", 3);

        assertThat(bossRepository.findCommonConditionsByBossId(easyBossId)).hasSize(1);
        assertThat(bossRepository.findCommonConditionsByBossId(normalBossId)).hasSize(2);
        assertThat(bossRepository.findCommonConditionsByBossId(hardBossId)).hasSize(3);
        assertThat(bossRepository.findCommonConditionsByBossId(hardBossId))
                .extracting(BossCommonConditionRow::getTargetType)
                .containsExactly("SUGAR_UNDER_LIMIT", "PROCESSED_DRINK_ZERO", "VEGETABLE_VARIETY");
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

    private void insertCondition(
            Long seasonId,
            Long bossId,
            String title,
            String targetType,
            int sortOrder
    ) {
        jdbcTemplate.update(
                """
                INSERT INTO boss_common_conditions (
                    season_id,
                    boss_id,
                    title,
                    description,
                    target_type,
                    threshold_value,
                    threshold_unit,
                    target_value,
                    required_days,
                    unit,
                    sort_order
                )
                VALUES (?, ?, ?, '조건 설명', ?, 50, 'g', 4, 4, '일', ?)
                """,
                seasonId,
                bossId,
                title,
                targetType,
                sortOrder
        );
    }
}
