package com.nyamnyam.coach.boss.service;

import com.nyamnyam.coach.boss.dto.request.BossBattleCreateRequest;
import com.nyamnyam.coach.boss.dto.response.BossBattleCreateResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleDetailResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleHpResponse;
import com.nyamnyam.coach.boss.entity.BossBattle;
import com.nyamnyam.coach.boss.entity.BossBattleCondition;
import com.nyamnyam.coach.boss.entity.BossBattleParticipant;
import com.nyamnyam.coach.boss.repository.BossBattleParticipantRepository;
import com.nyamnyam.coach.boss.repository.BossBattleRepository;
import com.nyamnyam.coach.boss.repository.row.BossBattleConditionRow;
import com.nyamnyam.coach.boss.repository.row.BossBattleParticipantCountRow;
import com.nyamnyam.coach.boss.repository.row.BossBattleParticipantRow;
import com.nyamnyam.coach.boss.repository.row.BossBattleRow;
import com.nyamnyam.coach.boss.repository.row.BossCommonConditionRow;
import com.nyamnyam.coach.boss.repository.row.BossRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.BossErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.GuildErrorCode;
import com.nyamnyam.coach.guild.service.GuildValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BossBattleServiceTest {

    @Mock
    private BossBattleRepository bossBattleRepository;

    @Mock
    private BossBattleParticipantRepository bossBattleParticipantRepository;

    @Mock
    private GuildValidator guildValidator;

    private BossBattleService bossBattleService;

    @BeforeEach
    void setUp() {
        bossBattleService = new BossBattleService(
                bossBattleRepository,
                bossBattleParticipantRepository,
                guildValidator
        );
    }

    @Test
    @DisplayName("길드장은 보스전을 생성하고 공통 조건을 복사할 수 있다")
    void createBossBattle() {
        when(bossBattleRepository.findActiveBossById(1L)).thenReturn(Optional.of(activeBoss()));
        when(bossBattleRepository.findCurrentSeasonId()).thenReturn(Optional.of(10L));
        when(bossBattleRepository.existsInProgressBattleByGuildId(100L)).thenReturn(false);
        when(bossBattleRepository.existsBattleByGuildIdAndSeasonId(100L, 10L)).thenReturn(false);
        when(bossBattleParticipantRepository.findActiveGuildMembersForBattleSnapshot(100L))
                .thenReturn(participants(2));
        doAnswer(invocation -> {
            BossBattle battle = invocation.getArgument(0);
            battle.setBattleId(500L);
            return null;
        }).when(bossBattleRepository).insertBossBattle(any(BossBattle.class));
        when(bossBattleRepository.findBossCommonConditionsByBossId(1L)).thenReturn(List.of(condition()));
        when(bossBattleRepository.findBattleDetailById(500L)).thenReturn(Optional.of(battleRow(800, 800)));

        BossBattleCreateResponse response = bossBattleService.createBossBattle(
                100L,
                1L,
                new BossBattleCreateRequest(1L)
        );

        ArgumentCaptor<BossBattle> battleCaptor = ArgumentCaptor.forClass(BossBattle.class);
        ArgumentCaptor<BossBattleCondition> conditionCaptor = ArgumentCaptor.forClass(BossBattleCondition.class);
        verify(guildValidator).validateGuildOwner(100L, 1L);
        verify(bossBattleRepository).insertBossBattle(battleCaptor.capture());
        verify(bossBattleRepository).insertBossBattleCondition(conditionCaptor.capture());
        verify(bossBattleParticipantRepository, times(2)).insertBossBattleParticipant(any(BossBattleParticipant.class));
        assertThat(battleCaptor.getValue().getMaxHp()).isEqualTo(800);
        assertThat(battleCaptor.getValue().getCurrentHp()).isEqualTo(800);
        assertThat(conditionCaptor.getValue().getTitle()).isEqualTo("길드원 4명 이상 식단 기록");
        assertThat(conditionCaptor.getValue().getThresholdValue()).isEqualByComparingTo("4");
        assertThat(conditionCaptor.getValue().getRequiredDays()).isEqualTo(4);
        assertThat(conditionCaptor.getValue().getDamage()).isEqualTo(160);
        assertThat(response.battleId()).isEqualTo(500L);
        assertThat(response.status()).isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("난이도별 정책으로 보스전 maxHp를 계산한다")
    void createBossBattleCalculatesMaxHpByDifficulty() {
        assertBattleMaxHp("EASY", 3, 950);
        assertBattleMaxHp("NORMAL", 3, 1750);
        assertBattleMaxHp("HARD", 3, 3000);
    }

    @Test
    @DisplayName("공통 조건 damage 합계는 난이도별 공통 조건 비중과 일치한다")
    void createBossBattleDistributesCommonConditionDamage() {
        when(bossBattleRepository.findActiveBossById(1L)).thenReturn(Optional.of(activeBoss("HARD")));
        when(bossBattleRepository.findCurrentSeasonId()).thenReturn(Optional.of(10L));
        when(bossBattleRepository.existsInProgressBattleByGuildId(100L)).thenReturn(false);
        when(bossBattleRepository.existsBattleByGuildIdAndSeasonId(100L, 10L)).thenReturn(false);
        when(bossBattleParticipantRepository.findActiveGuildMembersForBattleSnapshot(100L))
                .thenReturn(participants(4));
        doAnswer(invocation -> {
            BossBattle battle = invocation.getArgument(0);
            battle.setBattleId(500L);
            return null;
        }).when(bossBattleRepository).insertBossBattle(any(BossBattle.class));
        when(bossBattleRepository.findBossCommonConditionsByBossId(1L))
                .thenReturn(List.of(condition(1), condition(2), condition(3)));
        when(bossBattleRepository.findBattleDetailById(500L)).thenReturn(Optional.of(battleRow(3400, 3400)));

        bossBattleService.createBossBattle(100L, 1L, new BossBattleCreateRequest(1L));

        ArgumentCaptor<BossBattleCondition> conditionCaptor = ArgumentCaptor.forClass(BossBattleCondition.class);
        verify(bossBattleRepository, times(3)).insertBossBattleCondition(conditionCaptor.capture());
        assertThat(conditionCaptor.getAllValues()).extracting(BossBattleCondition::getDamage)
                .containsExactly(454, 453, 453);
    }

    @Test
    @DisplayName("일반 멤버는 보스전을 생성할 수 없다")
    void createBossBattleOwnerOnly() {
        doThrow(new BusinessException(GuildErrorCode.GUILD_OWNER_ONLY))
                .when(guildValidator).validateGuildOwner(100L, 2L);

        assertThatThrownBy(() -> bossBattleService.createBossBattle(100L, 2L, new BossBattleCreateRequest(1L)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_OWNER_ONLY);
    }

    @Test
    @DisplayName("존재하지 않는 보스로 보스전을 생성할 수 없다")
    void createBossBattleBossNotFound() {
        when(bossBattleRepository.findActiveBossById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bossBattleService.createBossBattle(100L, 1L, new BossBattleCreateRequest(999L)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(BossErrorCode.BOSS_NOT_FOUND);
    }

    @Test
    @DisplayName("INACTIVE 보스로 보스전을 생성할 수 없다")
    void createBossBattleInactiveBoss() {
        BossRow boss = activeBoss();
        boss.setStatus("INACTIVE");
        when(bossBattleRepository.findActiveBossById(1L)).thenReturn(Optional.of(boss));

        assertThatThrownBy(() -> bossBattleService.createBossBattle(100L, 1L, new BossBattleCreateRequest(1L)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(BossErrorCode.BOSS_INACTIVE);
    }

    @Test
    @DisplayName("이미 진행 중인 보스전이 있으면 생성할 수 없다")
    void createBossBattleAlreadyInProgress() {
        when(bossBattleRepository.findActiveBossById(1L)).thenReturn(Optional.of(activeBoss()));
        when(bossBattleRepository.findCurrentSeasonId()).thenReturn(Optional.of(10L));
        when(bossBattleRepository.existsInProgressBattleByGuildId(100L)).thenReturn(true);

        assertThatThrownBy(() -> bossBattleService.createBossBattle(100L, 1L, new BossBattleCreateRequest(1L)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(BossErrorCode.BOSS_BATTLE_ALREADY_IN_PROGRESS);
    }

    @Test
    @DisplayName("같은 시즌에 보스전을 중복 생성할 수 없다")
    void createBossBattleAlreadyExistsInSeason() {
        when(bossBattleRepository.findActiveBossById(1L)).thenReturn(Optional.of(activeBoss()));
        when(bossBattleRepository.findCurrentSeasonId()).thenReturn(Optional.of(10L));
        when(bossBattleRepository.existsInProgressBattleByGuildId(100L)).thenReturn(false);
        when(bossBattleRepository.existsBattleByGuildIdAndSeasonId(100L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> bossBattleService.createBossBattle(100L, 1L, new BossBattleCreateRequest(1L)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(BossErrorCode.BOSS_BATTLE_ALREADY_EXISTS_IN_SEASON);
    }

    @Test
    @DisplayName("길드원은 보스전 상세과 HP를 조회할 수 있다")
    void getBattleDetailAndHp() {
        when(bossBattleRepository.findBattleDetailById(500L)).thenReturn(Optional.of(battleRow()));
        when(bossBattleRepository.findBattleConditionsByBattleId(500L)).thenReturn(List.of(conditionRow()));
        when(bossBattleRepository.findRecentDamageLogsByBattleId(500L, 10)).thenReturn(List.of());
        when(bossBattleRepository.findBattleHpById(500L)).thenReturn(Optional.of(battleRow()));
        when(bossBattleParticipantRepository.countParticipantsByBattleId(500L)).thenReturn(participantCounts(2, 2, 0));

        BossBattleDetailResponse detail = bossBattleService.getBossBattleDetail(500L, 1L);
        BossBattleHpResponse hp = bossBattleService.getBossBattleHp(500L, 1L);

        verify(guildValidator, times(2)).validateGuildMember(100L, 1L);
        assertThat(detail.commonConditions()).hasSize(1);
        assertThat(detail.commonConditions().get(0).damage()).isEqualTo(160);
        assertThat(detail.hpRate()).isEqualTo(80.0);
        assertThat(hp.currentHp()).isEqualTo(800);
    }

    private BossRow activeBoss() {
        return activeBoss("EASY");
    }

    private BossRow activeBoss(String difficulty) {
        BossRow row = new BossRow();
        row.setBossId(1L);
        row.setSeasonId(10L);
        row.setName("설탕 슬라임");
        row.setDifficulty(difficulty);
        row.setMaxHp(1000);
        row.setStatus("ACTIVE");
        return row;
    }

    private BossCommonConditionRow condition() {
        return condition(1);
    }

    private BossCommonConditionRow condition(int sortOrder) {
        BossCommonConditionRow row = new BossCommonConditionRow();
        row.setConditionId(20L + sortOrder);
        row.setTitle("길드원 4명 이상 식단 기록");
        row.setDescription("이번 시즌 동안 길드원 4명 이상이 식단을 기록해야 합니다.");
        row.setTargetType("DIET_RECORD_MEMBER_COUNT");
        row.setThresholdValue(BigDecimal.valueOf(4));
        row.setThresholdUnit("명");
        row.setTargetValue(4);
        row.setRequiredDays(4);
        row.setUnit("명");
        row.setSortOrder(sortOrder);
        return row;
    }

    private BossBattleConditionRow conditionRow() {
        BossBattleConditionRow row = new BossBattleConditionRow();
        row.setBattleConditionId(30L);
        row.setBattleId(500L);
        row.setConditionId(20L);
        row.setTitle("길드원 4명 이상 식단 기록");
        row.setTargetType("DIET_RECORD_MEMBER_COUNT");
        row.setThresholdValue(BigDecimal.valueOf(4));
        row.setThresholdUnit("명");
        row.setTargetValue(4);
        row.setRequiredDays(4);
        row.setCurrentValue(2);
        row.setDamage(160);
        row.setUnit("명");
        row.setCompleted(false);
        row.setSortOrder(1);
        return row;
    }

    private BossBattleRow battleRow() {
        return battleRow(1000, 800);
    }

    private BossBattleRow battleRow(int maxHp, int currentHp) {
        BossBattleRow row = new BossBattleRow();
        row.setBattleId(500L);
        row.setGuildId(100L);
        row.setGuildName("잘먹잘싸");
        row.setBossId(1L);
        row.setSeasonId(10L);
        row.setBossName("설탕 슬라임");
        row.setDifficulty("EASY");
        row.setBossImageUrl("https://example.com/boss.png");
        row.setStatus("IN_PROGRESS");
        row.setMaxHp(maxHp);
        row.setCurrentHp(currentHp);
        row.setTotalDamage(maxHp - currentHp);
        row.setStartedAt(LocalDateTime.of(2026, 6, 10, 10, 0));
        row.setEndsAt(LocalDateTime.of(2026, 6, 16, 23, 59));
        return row;
    }

    private void assertBattleMaxHp(String difficulty, int activeMemberCount, int expectedMaxHp) {
        when(bossBattleRepository.findActiveBossById(1L)).thenReturn(Optional.of(activeBoss(difficulty)));
        when(bossBattleRepository.findCurrentSeasonId()).thenReturn(Optional.of(10L));
        when(bossBattleRepository.existsInProgressBattleByGuildId(100L)).thenReturn(false);
        when(bossBattleRepository.existsBattleByGuildIdAndSeasonId(100L, 10L)).thenReturn(false);
        when(bossBattleParticipantRepository.findActiveGuildMembersForBattleSnapshot(100L))
                .thenReturn(participants(activeMemberCount));
        doAnswer(invocation -> {
            BossBattle battle = invocation.getArgument(0);
            battle.setBattleId(500L);
            return null;
        }).when(bossBattleRepository).insertBossBattle(any(BossBattle.class));
        when(bossBattleRepository.findBossCommonConditionsByBossId(1L)).thenReturn(List.of());
        when(bossBattleRepository.findBattleDetailById(500L))
                .thenReturn(Optional.of(battleRow(expectedMaxHp, expectedMaxHp)));

        BossBattleCreateResponse response = bossBattleService.createBossBattle(
                100L,
                1L,
                new BossBattleCreateRequest(1L)
        );

        assertThat(response.maxHp()).isEqualTo(expectedMaxHp);
        assertThat(response.currentHp()).isEqualTo(expectedMaxHp);
    }

    private List<BossBattleParticipantRow> participants(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(index -> participant((long) index))
                .toList();
    }

    private BossBattleParticipantRow participant(Long userId) {
        BossBattleParticipantRow row = new BossBattleParticipantRow();
        row.setGuildId(100L);
        row.setUserId(userId);
        row.setGuildMemberId(userId + 10);
        row.setRoleAtStart(userId == 1L ? "OWNER" : "MEMBER");
        row.setStatus("ACTIVE");
        row.setSnapshotNickname("참여자" + userId);
        row.setSnapshotCharacterId(userId + 100);
        row.setSnapshotCharacterName("냠냠이");
        row.setSnapshotCharacterLevel(7);
        return row;
    }

    private BossBattleParticipantCountRow participantCounts(
            int participantCount,
            int activeParticipantCount,
            int leftParticipantCount
    ) {
        BossBattleParticipantCountRow row = new BossBattleParticipantCountRow();
        row.setParticipantCount(participantCount);
        row.setActiveParticipantCount(activeParticipantCount);
        row.setLeftParticipantCount(leftParticipantCount);
        return row;
    }
}
