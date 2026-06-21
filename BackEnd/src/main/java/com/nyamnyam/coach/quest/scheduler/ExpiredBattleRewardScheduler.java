package com.nyamnyam.coach.quest.scheduler;

import com.nyamnyam.coach.quest.service.ExpiredBattleRewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ExpiredBattleRewardScheduler {

    private static final Logger log = LoggerFactory.getLogger(ExpiredBattleRewardScheduler.class);

    private final ExpiredBattleRewardService expiredBattleRewardService;

    public ExpiredBattleRewardScheduler(ExpiredBattleRewardService expiredBattleRewardService) {
        this.expiredBattleRewardService = expiredBattleRewardService;
    }

    @Scheduled(cron = "${nyamnyam.rewards.auto-grant-cron:0 0 * * * *}")
    public void grantExpiredBattleRewards() {
        int grantedCount = expiredBattleRewardService.grantExpiredRewards(LocalDateTime.now());
        if (grantedCount > 0) {
            log.info("Auto-granted {} expired boss battle rewards", grantedCount);
        }
    }
}
