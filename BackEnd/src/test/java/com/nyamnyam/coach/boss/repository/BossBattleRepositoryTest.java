package com.nyamnyam.coach.boss.repository;

import com.nyamnyam.coach.boss.entity.BossBattle;
import com.nyamnyam.coach.boss.entity.BossBattleCondition;
import com.nyamnyam.coach.boss.repository.row.BossBattleRow;
import com.nyamnyam.coach.boss.repository.row.BossCommonConditionRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@Sql(scripts = "/test-auth-schema.sql")
class BossBattleRepositoryTest {

    private final BossBattleRepository bossBattleRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    BossBattleRepositoryTest(
            BossBattleRepository bossBattleRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.bossBattleRepository = bossBattleRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    @DisplayName("보스전을 생성하고 현재 보스전과 HP를 조회한다")
    void insertAndFindCurrentBattle() {
        Fixture fixture = fixture();
        BossBattle battle = battle(fixture.guildId(), fixture.bossId(), fixture.seasonId());

        bossBattleRepository.insertBossBattle(battle);

        assertThat(battle.getBattleId()).isNotNull();
        assertThat(bossBattleRepository.existsInProgressBattleByGuildId(fixture.guildId())).isTrue();
        assertThat(bossBattleRepository.existsBattleByGuildIdAndSeasonId(fixture.guildId(), fixture.seasonId())).isTrue();

        BossBattleRow current = bossBattleRepository.findCurrentBattleByGuildId(fixture.guildId()).get();
        BossBattleRow hp = bossBattleRepository.findBattleHpById(battle.getBattleId()).get();

        assertThat(current.getBossName()).isEqualTo("설탕 슬라임");
        assertThat(hp.getCurrentHp()).isEqualTo(1000);
        assertThat(bossBattleRepository.countActiveGuildMembers(fixture.guildId())).isEqualTo(1);
    }

    @Test
    @DisplayName("보스전 생성 시 공통 조건을 복사해 조회할 수 있다")
    void copyBattleConditions() {
        Fixture fixture = fixture();
        BossBattle battle = battle(fixture.guildId(), fixture.bossId(), fixture.seasonId());
        bossBattleRepository.insertBossBattle(battle);

        List<BossCommonConditionRow> commonConditions = bossBattleRepository.findBossCommonConditionsByBossId(fixture.bossId());
        for (BossCommonConditionRow commonCondition : commonConditions) {
            BossBattleCondition condition = new BossBattleCondition();
            condition.setBattleId(battle.getBattleId());
            condition.setConditionId(commonCondition.getConditionId());
            condition.setTitle(commonCondition.getTitle());
            condition.setDescription(commonCondition.getDescription());
            condition.setTargetType(commonCondition.getTargetType());
            condition.setThresholdValue(commonCondition.getThresholdValue());
            condition.setThresholdUnit(commonCondition.getThresholdUnit());
            condition.setTargetValue(commonCondition.getTargetValue());
            condition.setRequiredDays(commonCondition.getRequiredDays());
            condition.setCurrentValue(0);
            condition.setDamage(160);
            condition.setUnit(commonCondition.getUnit());
            condition.setCompleted(false);
            condition.setSortOrder(commonCondition.getSortOrder());
            bossBattleRepository.insertBossBattleCondition(condition);
        }

        assertThat(bossBattleRepository.findBattleConditionsByBattleId(battle.getBattleId())).hasSize(1);
        assertThat(bossBattleRepository.findBattleConditionsByBattleId(battle.getBattleId()).get(0).getTitle())
                .isEqualTo("길드원 4명 이상 식단 기록");
        assertThat(bossBattleRepository.findBattleConditionsByBattleId(battle.getBattleId()).get(0).getDamage())
                .isEqualTo(160);
    }

    @Test
    @DisplayName("같은 시즌이어도 선택한 보스 난이도 조건만 복사한다")
    void copyOnlySelectedBossDifficultyConditions() {
        Long ownerId = insertUser("difficulty-owner@example.com", "예린");
        Long guildId = insertGuild(ownerId);
        insertGuildMember(guildId, ownerId, "OWNER");
        Long seasonId = insertSeason();
        Long easyBossId = insertBoss(seasonId, "EASY", 50);
        Long normalBossId = insertBoss(seasonId, "NORMAL", 100);
        Long hardBossId = insertBoss(seasonId, "HARD", 200);

        insertCommonCondition(seasonId, easyBossId, "당류 50g 이하 유지", "SUGAR_UNDER_LIMIT", 50, "g", 3, 3, "일", 1);
        insertCommonCondition(seasonId, normalBossId, "당류 50g 이하 유지", "SUGAR_UNDER_LIMIT", 50, "g", 4, 4, "일", 1);
        insertCommonCondition(seasonId, normalBossId, "가공음료 0회", "PROCESSED_DRINK_ZERO", 0, "회", 4, 4, "일", 2);
        insertCommonCondition(seasonId, hardBossId, "당류 50g 이하 유지", "SUGAR_UNDER_LIMIT", 50, "g", 4, 4, "일", 1);
        insertCommonCondition(seasonId, hardBossId, "가공음료 0회", "PROCESSED_DRINK_ZERO", 0, "회", 4, 4, "일", 2);
        insertCommonCondition(seasonId, hardBossId, "채소 하루 2종 이상", "VEGETABLE_VARIETY", 2, "종", 5, 5, "일", 3);

        assertThat(bossBattleRepository.findBossCommonConditionsByBossId(easyBossId)).hasSize(1);
        assertThat(bossBattleRepository.findBossCommonConditionsByBossId(normalBossId)).hasSize(2);
        List<BossCommonConditionRow> hardConditions = bossBattleRepository.findBossCommonConditionsByBossId(hardBossId);
        assertThat(hardConditions).hasSize(3);
        assertThat(hardConditions)
                .extracting(BossCommonConditionRow::getTargetType)
                .containsExactly("SUGAR_UNDER_LIMIT", "PROCESSED_DRINK_ZERO", "VEGETABLE_VARIETY");

        BossBattle battle = battle(guildId, hardBossId, seasonId);
        bossBattleRepository.insertBossBattle(battle);
        for (BossCommonConditionRow commonCondition : hardConditions) {
            bossBattleRepository.insertBossBattleCondition(toBattleCondition(battle.getBattleId(), commonCondition));
        }

        var copiedConditions = bossBattleRepository.findBattleConditionsByBattleId(battle.getBattleId());
        assertThat(copiedConditions).hasSize(3);
        assertThat(copiedConditions)
                .extracting(row -> row.getTargetType())
                .containsExactly("SUGAR_UNDER_LIMIT", "PROCESSED_DRINK_ZERO", "VEGETABLE_VARIETY");
        assertThat(copiedConditions.get(0).getThresholdValue()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(copiedConditions.get(0).getThresholdUnit()).isEqualTo("g");
        assertThat(copiedConditions.get(0).getRequiredDays()).isEqualTo(4);
        assertThat(copiedConditions.get(0).getDamage()).isEqualTo(100);
    }

    @Test
    @DisplayName("보스전 상세, 최근 데미지 로그, 이력을 조회한다")
    void findDetailDamageLogsAndHistory() {
        Fixture fixture = fixture();
        BossBattle battle = battle(fixture.guildId(), fixture.bossId(), fixture.seasonId());
        bossBattleRepository.insertBossBattle(battle);
        insertDamageLog(battle.getBattleId(), fixture.ownerId());

        BossBattleRow detail = bossBattleRepository.findBattleDetailById(battle.getBattleId()).get();

        assertThat(detail.getGuildName()).isEqualTo("잘먹잘싸");
        assertThat(bossBattleRepository.findRecentDamageLogsByBattleId(battle.getBattleId(), 10)).hasSize(1);
        assertThat(bossBattleRepository.findBattleHistoryByGuildId(fixture.guildId(), 10, 0)).hasSize(1);
        assertThat(bossBattleRepository.countBattleHistoryByGuildId(fixture.guildId())).isEqualTo(1);
        assertThat(bossBattleRepository.findGuildIdByBattleId(battle.getBattleId())).contains(fixture.guildId());
    }

    private Fixture fixture() {
        Long ownerId = insertUser("owner@example.com", "예린");
        Long guildId = insertGuild(ownerId);
        insertGuildMember(guildId, ownerId, "OWNER");
        Long seasonId = insertSeason();
        Long bossId = insertBoss(seasonId);
        insertCommonCondition(seasonId, bossId);
        return new Fixture(ownerId, guildId, seasonId, bossId);
    }

    private Long insertUser(String email, String nickname) {
        jdbcTemplate.update(
                """
                INSERT INTO users (
                    email,
                    password_hash,
                    nickname,
                    status
                )
                VALUES (?, 'encoded-password', ?, 'ACTIVE')
                """,
                email,
                nickname
        );
        return jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = ?", Long.class, email);
    }

    private Long insertGuild(Long ownerId) {
        jdbcTemplate.update(
                """
                INSERT INTO guilds (
                    name,
                    description,
                    invite_code,
                    owner_user_id,
                    max_members,
                    visibility,
                    status
                )
                VALUES ('잘먹잘싸', '건강하게 먹는 길드', 'NYAM-A7K3', ?, 30, 'PRIVATE', 'ACTIVE')
                """,
                ownerId
        );
        return jdbcTemplate.queryForObject("SELECT guild_id FROM guilds WHERE invite_code = 'NYAM-A7K3'", Long.class);
    }

    private void insertGuildMember(Long guildId, Long userId, String role) {
        jdbcTemplate.update(
                "INSERT INTO guild_members (guild_id, user_id, role) VALUES (?, ?, ?)",
                guildId,
                userId,
                role
        );
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

    private Long insertBoss(Long seasonId) {
        return insertBoss(seasonId, "EASY", 1000);
    }

    private Long insertBoss(Long seasonId, String difficulty, int maxHp) {
        jdbcTemplate.update(
                """
                INSERT INTO bosses (
                    season_id,
                    name,
                    description,
                    difficulty,
                    max_hp,
                    reward_exp,
                    reward_coin,
                    status,
                    starts_at,
                    ends_at
                )
                VALUES (?, '설탕 슬라임', '초급 보스', ?, ?, 100, 50, 'ACTIVE', DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', 6, CURRENT_TIMESTAMP))
                """,
                seasonId,
                difficulty,
                maxHp
        );
        return jdbcTemplate.queryForObject("SELECT boss_id FROM bosses WHERE season_id = ? AND difficulty = ?", Long.class, seasonId, difficulty);
    }

    private void insertCommonCondition(Long seasonId, Long bossId) {
        insertCommonCondition(
                seasonId,
                bossId,
                "길드원 4명 이상 식단 기록",
                "DIET_RECORD_MEMBER_COUNT",
                4,
                "명",
                4,
                4,
                "명",
                1
        );
    }

    private void insertCommonCondition(
            Long seasonId,
            Long bossId,
            String title,
            String targetType,
            int thresholdValue,
            String thresholdUnit,
            int targetValue,
            int requiredDays,
            String unit,
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
                VALUES (?, ?, ?, '조건 설명', ?, ?, ?, ?, ?, ?, ?)
                """,
                seasonId,
                bossId,
                title,
                targetType,
                thresholdValue,
                thresholdUnit,
                targetValue,
                requiredDays,
                unit,
                sortOrder
        );
    }

    private BossBattleCondition toBattleCondition(Long battleId, BossCommonConditionRow commonCondition) {
        BossBattleCondition condition = new BossBattleCondition();
        condition.setBattleId(battleId);
        condition.setConditionId(commonCondition.getConditionId());
        condition.setTitle(commonCondition.getTitle());
        condition.setDescription(commonCondition.getDescription());
        condition.setTargetType(commonCondition.getTargetType());
        condition.setThresholdValue(commonCondition.getThresholdValue());
        condition.setThresholdUnit(commonCondition.getThresholdUnit());
        condition.setTargetValue(commonCondition.getTargetValue());
        condition.setRequiredDays(commonCondition.getRequiredDays());
        condition.setCurrentValue(0);
        condition.setDamage(100);
        condition.setUnit(commonCondition.getUnit());
        condition.setCompleted(false);
        condition.setSortOrder(commonCondition.getSortOrder());
        return condition;
    }

    private BossBattle battle(Long guildId, Long bossId, Long seasonId) {
        BossBattle battle = new BossBattle();
        battle.setGuildId(guildId);
        battle.setBossId(bossId);
        battle.setSeasonId(seasonId);
        battle.setStatus("IN_PROGRESS");
        battle.setMaxHp(1000);
        battle.setCurrentHp(1000);
        battle.setTotalDamage(0);
        return battle;
    }

    private void insertDamageLog(Long battleId, Long userId) {
        jdbcTemplate.update(
                """
                INSERT INTO boss_battle_damage_logs (
                    battle_id,
                    user_id,
                    damage,
                    source_type,
                    description
                )
                VALUES (?, ?, 100, 'PERSONAL_QUEST', '개인 퀘스트 완료')
                """,
                battleId,
                userId
        );
    }

    private record Fixture(Long ownerId, Long guildId, Long seasonId, Long bossId) {
    }
}
