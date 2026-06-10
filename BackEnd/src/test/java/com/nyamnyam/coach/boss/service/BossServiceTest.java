package com.nyamnyam.coach.boss.service;

import com.nyamnyam.coach.boss.dto.response.BossDetailResponse;
import com.nyamnyam.coach.boss.dto.response.CurrentBossResponse;
import com.nyamnyam.coach.boss.repository.BossRepository;
import com.nyamnyam.coach.boss.repository.row.BossCommonConditionRow;
import com.nyamnyam.coach.boss.repository.row.BossRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.BossErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BossServiceTest {

    @Mock
    private BossRepository bossRepository;

    private BossService bossService;

    @BeforeEach
    void setUp() {
        bossService = new BossService(bossRepository);
    }

    @Test
    @DisplayName("현재 시즌 보스 목록과 공통 격파 조건을 조회한다")
    void getCurrentBosses() {
        when(bossRepository.findCurrentBosses()).thenReturn(List.of(
                bossRow(1L, "EASY", "설탕 슬라임", 1000),
                bossRow(2L, "NORMAL", "야식 골렘", 3000),
                bossRow(3L, "HARD", "폭식 드래곤", 7000)
        ));
        when(bossRepository.findCommonConditionsBySeasonId(1L))
                .thenReturn(List.of(conditionRow()));

        CurrentBossResponse response = bossService.getCurrentBosses();

        assertThat(response.seasonId()).isEqualTo(1L);
        assertThat(response.bosses()).extracting("difficulty").containsExactly("EASY", "NORMAL", "HARD");
        assertThat(response.commonConditions()).hasSize(1);
        assertThat(response.commonConditions().get(0).targetType()).isEqualTo("DIET_RECORD_MEMBER_COUNT");
    }

    @Test
    @DisplayName("현재 활성화된 보스가 없으면 예외를 던진다")
    void getCurrentBossesNotFound() {
        when(bossRepository.findCurrentBosses()).thenReturn(List.of());

        assertThatThrownBy(() -> bossService.getCurrentBosses())
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(BossErrorCode.CURRENT_BOSS_NOT_FOUND);
    }

    @Test
    @DisplayName("특정 bossId로 보스 상세 정보를 조회한다")
    void getBossDetail() {
        when(bossRepository.findBossById(1L)).thenReturn(Optional.of(bossRow(1L, "EASY", "설탕 슬라임", 1000)));
        when(bossRepository.findCommonConditionsBySeasonId(1L))
                .thenReturn(List.of(conditionRow()));

        BossDetailResponse response = bossService.getBossDetail(1L);

        assertThat(response.bossId()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.commonConditions()).hasSize(1);
    }

    @Test
    @DisplayName("존재하지 않는 bossId 조회 시 예외를 던진다")
    void getBossDetailNotFound() {
        when(bossRepository.findBossById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bossService.getBossDetail(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(BossErrorCode.BOSS_NOT_FOUND);
    }

    @Test
    @DisplayName("INACTIVE 보스 조회 시 예외를 던진다")
    void getBossDetailInactive() {
        BossRow boss = bossRow(1L, "EASY", "설탕 슬라임", 1000);
        boss.setStatus("INACTIVE");
        when(bossRepository.findBossById(1L)).thenReturn(Optional.of(boss));

        assertThatThrownBy(() -> bossService.getBossDetail(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(BossErrorCode.BOSS_INACTIVE);
    }

    private BossRow bossRow(Long bossId, String difficulty, String name, int maxHp) {
        BossRow row = new BossRow();
        row.setBossId(bossId);
        row.setSeasonId(1L);
        row.setSeasonName("2026년 6월 2주차");
        row.setName(name);
        row.setDescription(name + " 설명");
        row.setDifficulty(difficulty);
        row.setMaxHp(maxHp);
        row.setImageUrl("https://example.com/boss.png");
        row.setRewardExp(100);
        row.setRewardCoin(50);
        row.setStatus("ACTIVE");
        row.setStartsAt(LocalDateTime.of(2026, 6, 10, 0, 0));
        row.setEndsAt(LocalDateTime.of(2026, 6, 16, 23, 59, 59));
        return row;
    }

    private BossCommonConditionRow conditionRow() {
        BossCommonConditionRow row = new BossCommonConditionRow();
        row.setConditionId(1L);
        row.setSeasonId(1L);
        row.setTitle("길드원 4명 이상 식단 기록");
        row.setDescription("이번 시즌 동안 길드원 4명 이상이 식단을 기록해야 합니다.");
        row.setTargetType("DIET_RECORD_MEMBER_COUNT");
        row.setTargetValue(4);
        row.setUnit("명");
        row.setSortOrder(1);
        return row;
    }
}
