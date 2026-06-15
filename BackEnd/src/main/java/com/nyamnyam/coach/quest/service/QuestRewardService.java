package com.nyamnyam.coach.quest.service;

import com.nyamnyam.coach.boss.dto.response.RewardClaimResponse;
import com.nyamnyam.coach.boss.entity.BossBattleStatus;
import com.nyamnyam.coach.quest.entity.Quest;
import com.nyamnyam.coach.quest.entity.QuestStatus;
import com.nyamnyam.coach.boss.entity.RewardClaim;
import com.nyamnyam.coach.boss.entity.RewardClaimSourceType;
import com.nyamnyam.coach.quest.repository.QuestRepository;
import com.nyamnyam.coach.boss.repository.row.BattleStateRow;
import com.nyamnyam.coach.character.entity.XpSourceType;
import com.nyamnyam.coach.character.service.CharacterGrowthService;
import com.nyamnyam.coach.coin.entity.CoinSourceType;
import com.nyamnyam.coach.coin.service.CoinService;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.BossErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.QuestErrorCode;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class QuestRewardService {

    private final QuestRepository questRepository;
    private final CharacterGrowthService characterGrowthService;
    private final CoinService coinService;

    public QuestRewardService(
            QuestRepository questRepository,
            CharacterGrowthService characterGrowthService,
            CoinService coinService
    ) {
        this.questRepository = questRepository;
        this.characterGrowthService = characterGrowthService;
        this.coinService = coinService;
    }

    @Transactional
    public RewardClaimResponse claimQuestReward(Long questId, Long userId) {
        Quest quest = questRepository.findQuestForUpdate(questId)
                .orElseThrow(() -> new BusinessException(QuestErrorCode.QUEST_NOT_FOUND));
        if (!Objects.equals(quest.getUserId(), userId)) {
            throw new BusinessException(QuestErrorCode.QUEST_NOT_OWNER);
        }
        if (QuestStatus.REWARDED.name().equals(quest.getStatus()) || quest.getRewardedAt() != null) {
            throw new BusinessException(QuestErrorCode.QUEST_REWARD_ALREADY_CLAIMED);
        }
        if (!QuestStatus.COMPLETED.name().equals(quest.getStatus())) {
            throw new BusinessException(QuestErrorCode.QUEST_REWARD_NOT_CLAIMABLE);
        }

        RewardClaimSourceType sourceType = RewardClaimSourceType.QUEST;
        validateRewardNotClaimed(userId, sourceType, questId, QuestErrorCode.QUEST_REWARD_ALREADY_CLAIMED);
        insertRewardClaim(
                userId,
                sourceType,
                questId,
                defaultValue(quest.getRewardExp()),
                defaultValue(quest.getRewardCoin())
        );

        if (defaultValue(quest.getRewardExp()) > 0) {
            characterGrowthService.addXp(
                    userId,
                    XpSourceType.QUEST,
                    questId,
                    quest.getRewardExp(),
                    "퀘스트 완료 보상"
            );
        }
        if (defaultValue(quest.getRewardCoin()) > 0) {
            coinService.earn(
                    userId,
                    quest.getRewardCoin(),
                    CoinSourceType.QUEST_REWARD,
                    questId,
                    "퀘스트 완료 보상"
            );
        }

        int updated = questRepository.updateQuestRewarded(questId);
        if (updated == 0) {
            throw new BusinessException(QuestErrorCode.QUEST_REWARD_ALREADY_CLAIMED);
        }
        return new RewardClaimResponse(
                sourceType.name(),
                questId,
                defaultValue(quest.getRewardExp()),
                defaultValue(quest.getRewardCoin()),
                LocalDateTime.now()
        );
    }

    @Transactional
    public RewardClaimResponse claimBossBattleReward(Long battleId, Long userId) {
        BattleStateRow battle = questRepository.findBattleRewardInfo(battleId)
                .orElseThrow(() -> new BusinessException(BossErrorCode.BOSS_BATTLE_NOT_FOUND));
        if (!questRepository.existsActiveGuildMember(battle.getGuildId(), userId)) {
            throw new BusinessException(BossErrorCode.BOSS_BATTLE_ACCESS_DENIED);
        }
        if (!BossBattleStatus.DEFEATED.name().equals(battle.getStatus())) {
            throw new BusinessException(BossErrorCode.BOSS_BATTLE_NOT_DEFEATED);
        }

        RewardClaimSourceType sourceType = RewardClaimSourceType.BOSS_BATTLE;
        validateRewardNotClaimed(userId, sourceType, battleId, BossErrorCode.BOSS_BATTLE_REWARD_ALREADY_CLAIMED);
        insertRewardClaim(
                userId,
                sourceType,
                battleId,
                defaultValue(battle.getRewardExp()),
                defaultValue(battle.getRewardCoin())
        );

        if (defaultValue(battle.getRewardExp()) > 0) {
            characterGrowthService.addXp(
                    userId,
                    XpSourceType.BOSS_BATTLE,
                    battleId,
                    battle.getRewardExp(),
                    "보스전 클리어 보상"
            );
        }
        if (defaultValue(battle.getRewardCoin()) > 0) {
            coinService.earn(
                    userId,
                    battle.getRewardCoin(),
                    CoinSourceType.BOSS_REWARD,
                    battleId,
                    "보스전 클리어 보상"
            );
        }

        return new RewardClaimResponse(
                sourceType.name(),
                battleId,
                defaultValue(battle.getRewardExp()),
                defaultValue(battle.getRewardCoin()),
                LocalDateTime.now()
        );
    }

    private void validateRewardNotClaimed(
            Long userId,
            RewardClaimSourceType sourceType,
            Long sourceId,
            com.nyamnyam.coach.global.exception.errorcode.ErrorCode errorCode
    ) {
        if (questRepository.existsRewardClaim(userId, sourceType.name(), sourceId)) {
            throw new BusinessException(errorCode);
        }
    }

    private void insertRewardClaim(
            Long userId,
            RewardClaimSourceType sourceType,
            Long sourceId,
            int xpAmount,
            int coinAmount
    ) {
        RewardClaim rewardClaim = new RewardClaim();
        rewardClaim.setUserId(userId);
        rewardClaim.setSourceType(sourceType.name());
        rewardClaim.setSourceId(sourceId);
        rewardClaim.setXpAmount(xpAmount);
        rewardClaim.setGuildPoint(0);
        rewardClaim.setCoinAmount(coinAmount);
        try {
            questRepository.insertRewardClaim(rewardClaim);
        } catch (DuplicateKeyException exception) {
            if (RewardClaimSourceType.QUEST.equals(sourceType)) {
                throw new BusinessException(QuestErrorCode.QUEST_REWARD_ALREADY_CLAIMED);
            }
            throw new BusinessException(BossErrorCode.BOSS_BATTLE_REWARD_ALREADY_CLAIMED);
        }
    }

    private int defaultValue(Integer value) {
        return value == null ? 0 : value;
    }
}
