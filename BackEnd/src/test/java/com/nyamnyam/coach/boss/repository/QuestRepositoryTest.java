package com.nyamnyam.coach.boss.repository;

import com.nyamnyam.coach.boss.entity.Quest;
import com.nyamnyam.coach.boss.repository.row.QuestRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@Sql(scripts = "/test-auth-schema.sql")
class QuestRepositoryTest {

    private final QuestRepository questRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    QuestRepositoryTest(
            QuestRepository questRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.questRepository = questRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    @DisplayName("퀘스트를 생성하고 전체 목록과 내 퀘스트를 조회한다")
    void insertAndFindQuests() {
        Fixture fixture = fixture();
        Quest quest = quest(fixture.battleId(), fixture.guildId(), fixture.ownerId());

        questRepository.insertQuest(quest);

        assertThat(quest.getQuestId()).isNotNull();
        assertThat(questRepository.existsQuestByBattleIdAndUserId(fixture.battleId(), fixture.ownerId())).isTrue();
        assertThat(questRepository.countQuestsByBattleId(fixture.battleId())).isEqualTo(1);

        List<QuestRow> quests = questRepository.findQuestsByBattleId(fixture.battleId(), fixture.ownerId());

        assertThat(quests).hasSize(1);
        assertThat(quests.get(0).getIsMe()).isTrue();
        assertThat(questRepository.findMyQuestByBattleId(fixture.battleId(), fixture.ownerId())).isPresent();
    }

    @Test
    @DisplayName("퀘스트 상세와 기여도를 조회한다")
    void findQuestDetailAndContributions() {
        Fixture fixture = fixture();
        Quest quest = quest(fixture.battleId(), fixture.guildId(), fixture.ownerId());
        questRepository.insertQuest(quest);

        QuestRow detail = questRepository.findQuestDetailById(quest.getQuestId(), fixture.ownerId()).get();

        assertThat(detail.getNickname()).isEqualTo("예린");
        assertThat(questRepository.findBattleById(fixture.battleId())).isPresent();
        assertThat(questRepository.findGuildIdByBattleId(fixture.battleId())).contains(fixture.guildId());
        assertThat(questRepository.existsActiveGuildMember(fixture.guildId(), fixture.ownerId())).isTrue();
        assertThat(questRepository.findGuildRole(fixture.guildId(), fixture.ownerId())).contains("OWNER");
        assertThat(questRepository.findActiveGuildMembers(fixture.guildId())).hasSize(2);
        assertThat(questRepository.findQuestContributionsByBattleId(fixture.battleId(), fixture.ownerId())).hasSize(2);
    }

    private Fixture fixture() {
        Long ownerId = insertUser("owner@example.com", "예린");
        Long memberId = insertUser("member@example.com", "민수");
        insertCharacter(ownerId, "냠냠이");
        insertCharacter(memberId, "민냠이");
        Long guildId = insertGuild(ownerId);
        insertGuildMember(guildId, ownerId, "OWNER");
        insertGuildMember(guildId, memberId, "MEMBER");
        Long seasonId = insertSeason();
        Long bossId = insertBoss(seasonId);
        Long battleId = insertBattle(guildId, bossId, seasonId);
        return new Fixture(ownerId, guildId, battleId);
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

    private void insertCharacter(Long userId, String name) {
        jdbcTemplate.update(
                """
                INSERT INTO characters (
                    user_id,
                    name,
                    level
                )
                VALUES (?, ?, 7)
                """,
                userId,
                name
        );
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
                    status
                )
                VALUES (?, '설탕 슬라임', '초급 보스', 'EASY', 1000, 100, 50, 'ACTIVE')
                """,
                seasonId
        );
        return jdbcTemplate.queryForObject("SELECT boss_id FROM bosses WHERE season_id = ? AND difficulty = 'EASY'", Long.class, seasonId);
    }

    private Long insertBattle(Long guildId, Long bossId, Long seasonId) {
        jdbcTemplate.update(
                """
                INSERT INTO boss_battles (
                    guild_id,
                    boss_id,
                    season_id,
                    status,
                    max_hp,
                    current_hp,
                    total_damage
                )
                VALUES (?, ?, ?, 'IN_PROGRESS', 1000, 1000, 0)
                """,
                guildId,
                bossId,
                seasonId
        );
        return jdbcTemplate.queryForObject("SELECT battle_id FROM boss_battles WHERE guild_id = ?", Long.class, guildId);
    }

    private Quest quest(Long battleId, Long guildId, Long userId) {
        Quest quest = new Quest();
        quest.setBattleId(battleId);
        quest.setGuildId(guildId);
        quest.setUserId(userId);
        quest.setTitle("오늘 식단 기록하기");
        quest.setDescription("오늘 하루 식단을 1회 이상 기록하세요.");
        quest.setQuestType("RECORD_DIET");
        quest.setTargetValue(1);
        quest.setCurrentValue(0);
        quest.setUnit("회");
        quest.setDamage(100);
        quest.setRewardExp(30);
        quest.setRewardCoin(10);
        quest.setStatus("IN_PROGRESS");
        quest.setSourceType("PLACEHOLDER");
        return quest;
    }

    private record Fixture(Long ownerId, Long guildId, Long battleId) {
    }
}
