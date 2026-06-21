package com.nyamnyam.coach.quest.service;

import com.nyamnyam.coach.boss.dto.response.RewardClaimResponse;
import com.nyamnyam.coach.boss.entity.RewardClaim;
import com.nyamnyam.coach.boss.repository.row.BattleStateRow;
import com.nyamnyam.coach.character.entity.XpSourceType;
import com.nyamnyam.coach.character.service.CharacterGrowthService;
import com.nyamnyam.coach.coin.service.CoinService;
import com.nyamnyam.coach.quest.entity.Quest;
import com.nyamnyam.coach.quest.repository.QuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestRewardServiceTest {

    @Mock
    private QuestRepository questRepository;

    @Mock
    private CharacterGrowthService characterGrowthService;

    @Mock
    private CoinService coinService;

    private QuestRewardService questRewardService;

    @BeforeEach
    void setUp() {
        questRewardService = new QuestRewardService(
                questRepository,
                characterGrowthService,
                coinService
        );
    }

    @Test
    @DisplayName("EASY 보스 보상 XP 800을 변환 없이 지급한다")
    void claimEasyBossRewardUsesFullXpAmount() {
        BattleStateRow battle = defeatedEasyBattle();
        when(questRepository.findBattleRewardInfo(50L)).thenReturn(Optional.of(battle));
        when(questRepository.existsActiveGuildMember(10L, 1L)).thenReturn(true);
        when(questRepository.existsRewardClaim(1L, "BOSS_BATTLE", 50L)).thenReturn(false);

        RewardClaimResponse response = questRewardService.claimBossBattleReward(50L, 1L);

        ArgumentCaptor<RewardClaim> claimCaptor = ArgumentCaptor.forClass(RewardClaim.class);
        verify(questRepository).insertRewardClaim(claimCaptor.capture());
        verify(characterGrowthService).addXp(
                1L,
                XpSourceType.BOSS_BATTLE,
                50L,
                800,
                "보스전 클리어 보상"
        );
        assertThat(claimCaptor.getValue().getXpAmount()).isEqualTo(800);
        assertThat(response.xpAmount()).isEqualTo(800);
    }

    @Test
    @DisplayName("자동 지급은 이미 지급된 퀘스트와 보스 보상을 중복 지급하지 않는다")
    void autoGrantSkipsExistingRewardClaims() {
        BattleStateRow battle = defeatedEasyBattle();
        Quest quest = new Quest();
        quest.setQuestId(70L);
        quest.setBattleId(50L);
        quest.setUserId(1L);
        quest.setStatus("COMPLETED");
        quest.setRewardExp(100);
        quest.setRewardCoin(20);
        when(questRepository.findBattleStateForUpdate(50L)).thenReturn(Optional.of(battle));
        when(questRepository.findCompletedUnrewardedQuestsByBattleId(50L)).thenReturn(List.of(quest));
        when(questRepository.findBattleParticipantUserIds(50L)).thenReturn(List.of(1L));
        when(questRepository.existsRewardClaim(1L, "QUEST", 70L)).thenReturn(true);
        when(questRepository.existsRewardClaim(1L, "BOSS_BATTLE", 50L)).thenReturn(true);

        int grantedCount = questRewardService.autoGrantExpiredBattleRewards(50L);

        verify(questRepository).updateQuestRewarded(70L);
        verify(questRepository, never()).insertRewardClaim(org.mockito.ArgumentMatchers.any(RewardClaim.class));
        verify(characterGrowthService, never()).addXp(
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any(XpSourceType.class),
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyInt(),
                org.mockito.ArgumentMatchers.anyString()
        );
        assertThat(grantedCount).isZero();
    }

    private BattleStateRow defeatedEasyBattle() {
        BattleStateRow row = new BattleStateRow();
        row.setBattleId(50L);
        row.setGuildId(10L);
        row.setBossId(5L);
        row.setStatus("DEFEATED");
        row.setRewardExp(800);
        row.setRewardCoin(300);
        return row;
    }
}
