package com.nyamnyam.coach.ranking.service;

import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.BossErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.RankingErrorCode;
import com.nyamnyam.coach.ranking.dto.response.BossRankingItemResponse;
import com.nyamnyam.coach.ranking.dto.response.BossRankingResponse;
import com.nyamnyam.coach.ranking.dto.response.GuildRankingItemResponse;
import com.nyamnyam.coach.ranking.dto.response.GuildRankingResponse;
import com.nyamnyam.coach.ranking.repository.RankingRepository;
import com.nyamnyam.coach.ranking.repository.row.BossInfoRow;
import com.nyamnyam.coach.ranking.repository.row.BossRankingRow;
import com.nyamnyam.coach.ranking.repository.row.GuildWeeklyStatRow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RankingService {

    private final RankingRepository rankingRepository;
    private final GuildScoreService guildScoreService;

    public RankingService(RankingRepository rankingRepository, GuildScoreService guildScoreService) {
        this.rankingRepository = rankingRepository;
        this.guildScoreService = guildScoreService;
    }

    @Transactional(readOnly = true)
    public GuildRankingResponse getGuildRankings(
            Long userId,
            LocalDate weekStartDate,
            LocalDate weekEndDate,
            Integer size
    ) {
        GuildScoreService.WeekPeriod period = resolveAndValidate(weekStartDate, weekEndDate);
        int limit = normalizeSize(size);
        Optional<Long> myGuildId = rankingRepository.findMyGuildId(userId);
        int elapsedDays = guildScoreService.calculateElapsedDaysInWeek(period.startDate(), period.endDate());

        List<GuildRankingItemResponse> ranked = new ArrayList<>();
        for (GuildWeeklyStatRow row : rankingRepository.findGuildWeeklyStats(period.startDate(), period.endDate())) {
            int activeMemberCount = value(row.getActiveMemberCount());
            double recordRate = guildScoreService.safeRate(
                    value(row.getRecordedMemberDateCount()),
                    activeMemberCount * elapsedDays
            );
            double questCompletionRate = guildScoreService.safeRate(
                    value(row.getQuestCompletedCount()),
                    value(row.getQuestTotalCount())
            );
            double bossProgressRate = guildScoreService.safeRate(
                    value(row.getBossDamage()),
                    value(row.getBossMaxHp())
            );
            int weeklyScore = guildScoreService.calculateWeeklyScore(
                    recordRate,
                    questCompletionRate,
                    bossProgressRate,
                    guildScoreService.calculateClearBonus(row.getDifficulty(), row.getBattleStatus())
            );
            ranked.add(new GuildRankingItemResponse(
                    0,
                    row.getGuildId(),
                    row.getGuildName(),
                    myGuildId.map(id -> id.equals(row.getGuildId())).orElse(false),
                    weeklyScore,
                    recordRate,
                    questCompletionRate,
                    value(row.getBossDamage())
            ));
        }

        ranked.sort(Comparator
                .comparingInt(GuildRankingItemResponse::weeklyScore).reversed()
                .thenComparing(Comparator.comparingDouble(GuildRankingItemResponse::recordRate).reversed())
                .thenComparing(Comparator.comparingDouble(GuildRankingItemResponse::questCompletionRate).reversed())
                .thenComparing(Comparator.comparingInt(GuildRankingItemResponse::bossDamage).reversed())
                .thenComparing(GuildRankingItemResponse::guildId));

        Integer myGuildRank = null;
        List<GuildRankingItemResponse> withRanks = new ArrayList<>();
        for (int i = 0; i < ranked.size(); i++) {
            GuildRankingItemResponse item = ranked.get(i);
            int rank = i + 1;
            if (item.myGuild()) {
                myGuildRank = rank;
            }
            if (withRanks.size() < limit) {
                withRanks.add(new GuildRankingItemResponse(
                        rank,
                        item.guildId(),
                        item.guildName(),
                        item.myGuild(),
                        item.weeklyScore(),
                        item.recordRate(),
                        item.questCompletionRate(),
                        item.bossDamage()
                ));
            }
        }
        return new GuildRankingResponse(period.startDate(), period.endDate(), myGuildRank, withRanks);
    }

    @Transactional(readOnly = true)
    public BossRankingResponse getBossRankings(Long userId, Long bossId) {
        BossInfoRow boss = rankingRepository.findBossInfo(bossId)
                .orElseThrow(() -> new BusinessException(BossErrorCode.BOSS_NOT_FOUND));
        Optional<Long> myGuildId = rankingRepository.findMyGuildId(userId);
        List<BossRankingItemResponse> rankings = new ArrayList<>();
        Integer myGuildRank = null;
        List<BossRankingRow> rows = rankingRepository.findBossRankingByBossId(bossId);
        for (int i = 0; i < rows.size(); i++) {
            BossRankingRow row = rows.get(i);
            int rank = i + 1;
            boolean myGuild = myGuildId.map(id -> id.equals(row.getGuildId())).orElse(false);
            if (myGuild) {
                myGuildRank = rank;
            }
            rankings.add(new BossRankingItemResponse(
                    rank,
                    row.getGuildId(),
                    row.getGuildName(),
                    myGuild,
                    row.getStatus(),
                    value(row.getMaxHp()),
                    value(row.getCurrentHp()),
                    value(row.getTotalDamage()),
                    hpRate(value(row.getCurrentHp()), value(row.getMaxHp())),
                    row.getStartedAt(),
                    row.getEndedAt()
            ));
        }
        return new BossRankingResponse(boss.getBossId(), boss.getBossName(), boss.getDifficulty(), myGuildRank, rankings);
    }

    private GuildScoreService.WeekPeriod resolveAndValidate(LocalDate start, LocalDate end) {
        try {
            GuildScoreService.WeekPeriod period = guildScoreService.resolveWeek(start, end);
            period.validate();
            return period;
        } catch (IllegalArgumentException e) {
            throw new BusinessException(RankingErrorCode.RANKING_PERIOD_INVALID);
        }
    }

    private int normalizeSize(Integer size) {
        if (size == null || size <= 0) {
            return 10;
        }
        return Math.min(size, 100);
    }

    private double hpRate(int currentHp, int maxHp) {
        return guildScoreService.safeRate(currentHp, maxHp);
    }

    private int value(Integer value) {
        return value == null ? 0 : value;
    }
}
