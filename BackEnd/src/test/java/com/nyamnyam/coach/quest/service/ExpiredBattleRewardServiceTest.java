package com.nyamnyam.coach.quest.service;

import com.nyamnyam.coach.quest.repository.QuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpiredBattleRewardServiceTest {

    @Mock
    private QuestRepository questRepository;

    @Mock
    private QuestRewardService questRewardService;

    private ExpiredBattleRewardService expiredBattleRewardService;

    @BeforeEach
    void setUp() {
        expiredBattleRewardService = new ExpiredBattleRewardService(
                questRepository,
                questRewardService
        );
    }

    @Test
    @DisplayName("종료된 보스전의 미수령 보상을 전투별 지급 서비스에 위임한다")
    void grantExpiredRewards() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 22, 0, 0);
        when(questRepository.findEndedBattleIdsPendingAutoRewards(now)).thenReturn(List.of(10L, 20L));
        when(questRewardService.autoGrantExpiredBattleRewards(10L)).thenReturn(3);
        when(questRewardService.autoGrantExpiredBattleRewards(20L)).thenReturn(2);

        int grantedCount = expiredBattleRewardService.grantExpiredRewards(now);

        verify(questRepository).expireEndedBattles(now);
        verify(questRewardService).autoGrantExpiredBattleRewards(10L);
        verify(questRewardService).autoGrantExpiredBattleRewards(20L);
        assertThat(grantedCount).isEqualTo(5);
    }
}
