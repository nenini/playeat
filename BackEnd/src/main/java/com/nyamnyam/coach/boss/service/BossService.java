package com.nyamnyam.coach.boss.service;

import com.nyamnyam.coach.boss.dto.response.BossCommonConditionResponse;
import com.nyamnyam.coach.boss.dto.response.BossDetailResponse;
import com.nyamnyam.coach.boss.dto.response.BossSummaryResponse;
import com.nyamnyam.coach.boss.dto.response.CurrentBossResponse;
import com.nyamnyam.coach.boss.entity.BossStatus;
import com.nyamnyam.coach.boss.repository.BossRepository;
import com.nyamnyam.coach.boss.repository.row.BossCommonConditionRow;
import com.nyamnyam.coach.boss.repository.row.BossRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.BossErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BossService {

    private final BossRepository bossRepository;

    public BossService(BossRepository bossRepository) {
        this.bossRepository = bossRepository;
    }

    @Transactional(readOnly = true)
    public CurrentBossResponse getCurrentBosses() {
        List<BossRow> bosses = bossRepository.findCurrentBosses();
        if (bosses.isEmpty()) {
            throw new BusinessException(BossErrorCode.CURRENT_BOSS_NOT_FOUND);
        }

        BossRow firstBoss = bosses.get(0);
        return new CurrentBossResponse(
                firstBoss.getSeasonId(),
                firstBoss.getSeasonName(),
                firstBoss.getStartsAt(),
                firstBoss.getEndsAt(),
                bosses.stream().map(this::toSummaryResponse).toList()
        );
    }

    @Transactional(readOnly = true)
    public BossDetailResponse getBossDetail(Long bossId) {
        BossRow boss = bossRepository.findBossById(bossId)
                .orElseThrow(() -> new BusinessException(BossErrorCode.BOSS_NOT_FOUND));
        if (!BossStatus.ACTIVE.name().equals(boss.getStatus())) {
            throw new BusinessException(BossErrorCode.BOSS_INACTIVE);
        }

        List<BossCommonConditionResponse> commonConditions = bossRepository
                .findCommonConditionsByBossId(boss.getBossId())
                .stream()
                .map(this::toConditionResponse)
                .toList();

        return new BossDetailResponse(
                boss.getBossId(),
                boss.getSeasonId(),
                boss.getName(),
                boss.getDescription(),
                boss.getDifficulty(),
                boss.getMaxHp(),
                boss.getImageUrl(),
                boss.getRewardExp(),
                boss.getRewardCoin(),
                boss.getStatus(),
                boss.getStartsAt(),
                boss.getEndsAt(),
                commonConditions
        );
    }

    private BossSummaryResponse toSummaryResponse(BossRow boss) {
        List<BossCommonConditionResponse> commonConditions = bossRepository
                .findCommonConditionsByBossId(boss.getBossId())
                .stream()
                .map(this::toConditionResponse)
                .toList();

        return new BossSummaryResponse(
                boss.getBossId(),
                boss.getName(),
                boss.getDescription(),
                boss.getDifficulty(),
                boss.getMaxHp(),
                boss.getImageUrl(),
                boss.getRewardExp(),
                boss.getRewardCoin(),
                commonConditions
        );
    }

    private BossCommonConditionResponse toConditionResponse(BossCommonConditionRow condition) {
        return new BossCommonConditionResponse(
                condition.getConditionId(),
                condition.getTitle(),
                condition.getDescription(),
                condition.getTargetType(),
                condition.getThresholdValue(),
                condition.getThresholdUnit(),
                condition.getTargetValue(),
                condition.getRequiredDays(),
                condition.getUnit(),
                condition.getSortOrder()
        );
    }
}
