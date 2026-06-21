package com.nyamnyam.coach.quest.service;

import com.nyamnyam.coach.quest.repository.QuestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ExpiredBattleRewardService {

    private static final Logger log = LoggerFactory.getLogger(ExpiredBattleRewardService.class);

    private final QuestRepository questRepository;
    private final QuestRewardService questRewardService;

    public ExpiredBattleRewardService(
            QuestRepository questRepository,
            QuestRewardService questRewardService
    ) {
        this.questRepository = questRepository;
        this.questRewardService = questRewardService;
    }

    public int grantExpiredRewards(LocalDateTime now) {
        questRepository.expireEndedBattles(now);
        int grantedCount = 0;
        for (Long battleId : questRepository.findEndedBattleIdsPendingAutoRewards(now)) {
            try {
                grantedCount += questRewardService.autoGrantExpiredBattleRewards(battleId);
            } catch (RuntimeException exception) {
                log.error("Failed to auto-grant rewards for boss battle {}", battleId, exception);
            }
        }
        return grantedCount;
    }
}
