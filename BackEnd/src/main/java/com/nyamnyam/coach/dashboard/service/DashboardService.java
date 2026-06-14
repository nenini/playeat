package com.nyamnyam.coach.dashboard.service;

import com.nyamnyam.coach.dashboard.dto.response.BossBattleDashboardResponse;
import com.nyamnyam.coach.dashboard.dto.response.DailyScoreResponse;
import com.nyamnyam.coach.dashboard.dto.response.GuildDailyStatResponse;
import com.nyamnyam.coach.dashboard.dto.response.GuildDashboardResponse;
import com.nyamnyam.coach.dashboard.dto.response.GuildWeeklyReportResponse;
import com.nyamnyam.coach.dashboard.repository.DashboardRepository;
import com.nyamnyam.coach.dashboard.repository.row.BattleDashboardRow;
import com.nyamnyam.coach.dashboard.repository.row.DailyCountRow;
import com.nyamnyam.coach.dashboard.repository.row.DailyScoreRow;
import com.nyamnyam.coach.dashboard.repository.row.GuildBasicInfoRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.BossErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.DashboardErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.GuildErrorCode;
import com.nyamnyam.coach.guild.service.GuildValidator;
import com.nyamnyam.coach.ranking.dto.response.GuildRankingItemResponse;
import com.nyamnyam.coach.ranking.dto.response.GuildRankingResponse;
import com.nyamnyam.coach.ranking.repository.GuildScoreRepository;
import com.nyamnyam.coach.ranking.service.GuildScoreService;
import com.nyamnyam.coach.ranking.service.RankingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private static final int RECORD_SCORE_PER_MEMBER = 30;

    private final DashboardRepository dashboardRepository;
    private final GuildScoreRepository guildScoreRepository;
    private final GuildScoreService guildScoreService;
    private final RankingService rankingService;
    private final GuildValidator guildValidator;

    public DashboardService(
            DashboardRepository dashboardRepository,
            GuildScoreRepository guildScoreRepository,
            GuildScoreService guildScoreService,
            RankingService rankingService,
            GuildValidator guildValidator
    ) {
        this.dashboardRepository = dashboardRepository;
        this.guildScoreRepository = guildScoreRepository;
        this.guildScoreService = guildScoreService;
        this.rankingService = rankingService;
        this.guildValidator = guildValidator;
    }

    @Transactional(readOnly = true)
    public GuildDashboardResponse getGuildDashboard(
            Long userId,
            Long guildId,
            LocalDate weekStartDate,
            LocalDate weekEndDate
    ) {
        GuildScoreService.WeekPeriod period = resolveAndValidate(weekStartDate, weekEndDate);
        guildValidator.validateGuildMember(guildId, userId);
        GuildBasicInfoRow guild = dashboardRepository.findGuildBasicInfo(guildId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_NOT_FOUND));
        WeeklyMetrics metrics = getWeeklyMetrics(guildId, period);
        GuildRankingResponse rankings = rankingService.getGuildRankings(userId, period.startDate(), period.endDate(), 100);
        Integer myRank = rankings.rankings().stream()
                .filter(item -> item.guildId().equals(guildId))
                .map(GuildRankingItemResponse::rank)
                .findFirst()
                .orElse(rankings.myGuildRank());
        return new GuildDashboardResponse(
                guild.getGuildId(),
                guild.getGuildName(),
                myRank,
                metrics.weeklyScore(),
                metrics.recordRate(),
                metrics.bossDamage(),
                metrics.questCompletedCount(),
                metrics.questTotalCount(),
                buildDailyScores(guildId, period)
        );
    }

    @Transactional(readOnly = true)
    public BossBattleDashboardResponse getBossBattleDashboard(Long userId, Long battleId) {
        BattleDashboardRow battle = dashboardRepository.findBattleDashboardById(battleId)
                .orElseThrow(() -> new BusinessException(BossErrorCode.BOSS_BATTLE_NOT_FOUND));
        guildValidator.validateGuildMember(battle.getGuildId(), userId);
        int questTotalCount = dashboardRepository.countQuestsByBattleId(battleId);
        int questCompletedCount = dashboardRepository.countCompletedQuestsByBattleId(battleId);
        int conditionTotalCount = dashboardRepository.countBattleConditionsByBattleId(battleId);
        int conditionCompletedCount = dashboardRepository.countCompletedBattleConditionsByBattleId(battleId);
        GuildScoreService.WeekPeriod period = guildScoreService.resolveWeek(null, null);
        WeeklyMetrics metrics = getWeeklyMetrics(battle.getGuildId(), period);
        return new BossBattleDashboardResponse(
                battle.getBattleId(),
                battle.getGuildId(),
                battle.getBossName(),
                battle.getDifficulty(),
                battle.getStatus(),
                value(battle.getMaxHp()),
                value(battle.getCurrentHp()),
                value(battle.getTotalDamage()),
                guildScoreService.safeRate(value(battle.getCurrentHp()), value(battle.getMaxHp())),
                questCompletedCount,
                questTotalCount,
                conditionCompletedCount,
                conditionTotalCount,
                metrics.weeklyScore()
        );
    }

    @Transactional(readOnly = true)
    public GuildWeeklyReportResponse getGuildWeeklyReport(
            Long userId,
            Long guildId,
            LocalDate weekStartDate,
            LocalDate weekEndDate
    ) {
        GuildScoreService.WeekPeriod period = resolveAndValidate(weekStartDate, weekEndDate);
        guildValidator.validateGuildMember(guildId, userId);
        GuildBasicInfoRow guild = dashboardRepository.findGuildBasicInfo(guildId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_NOT_FOUND));
        WeeklyMetrics metrics = getWeeklyMetrics(guildId, period);
        return new GuildWeeklyReportResponse(
                guild.getGuildId(),
                guild.getGuildName(),
                period.startDate(),
                period.endDate(),
                metrics.recordRate(),
                metrics.bossDamage(),
                metrics.weeklyScore(),
                metrics.questCompletedCount(),
                metrics.questTotalCount(),
                buildDailyStats(guildId, period)
        );
    }

    private WeeklyMetrics getWeeklyMetrics(Long guildId, GuildScoreService.WeekPeriod period) {
        int activeMemberCount = dashboardRepository.countActiveGuildMembers(guildId);
        int elapsedDays = guildScoreService.calculateElapsedDaysInWeek(period.startDate(), period.endDate());
        int recordedMemberDateCount = dashboardRepository.countRecordedMemberDates(
                guildId,
                period.startDate(),
                period.endDate()
        );
        double recordRate = guildScoreService.safeRate(recordedMemberDateCount, activeMemberCount * elapsedDays);
        int questTotalCount = dashboardRepository.countQuestsByGuildAndWeek(guildId, period.startDate(), period.endDate());
        int questCompletedCount = dashboardRepository.countCompletedQuestsByGuildAndWeek(
                guildId,
                period.startDate(),
                period.endDate()
        );
        double questCompletionRate = guildScoreService.safeRate(questCompletedCount, questTotalCount);
        BattleDashboardRow battle = dashboardRepository.findCurrentOrLatestBattleByGuildId(guildId).orElse(null);
        int bossDamage = battle == null ? 0 : value(battle.getTotalDamage());
        int bossMaxHp = battle == null ? 0 : value(battle.getMaxHp());
        double bossProgressRate = guildScoreService.safeRate(bossDamage, bossMaxHp);
        int clearBonus = battle == null ? 0 : guildScoreService.calculateClearBonus(battle.getDifficulty(), battle.getStatus());
        int weeklyScore = guildScoreService.calculateWeeklyScore(recordRate, questCompletionRate, bossProgressRate, clearBonus);
        return new WeeklyMetrics(recordRate, bossDamage, weeklyScore, questCompletedCount, questTotalCount);
    }

    private List<DailyScoreResponse> buildDailyScores(Long guildId, GuildScoreService.WeekPeriod period) {
        Map<LocalDate, Integer> recordCounts = toDailyCountMap(
                dashboardRepository.findDailyRecordedMembers(guildId, period.startDate(), period.endDate())
        );
        Map<LocalDate, Integer> logScores = toDailyScoreMap(
                guildScoreRepository.findDailyScores(guildId, period.startDate(), period.endDate())
        );
        List<DailyScoreResponse> responses = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = period.startDate().plusDays(i);
            int score = logScores.getOrDefault(date, 0);
            if (score == 0) {
                score = recordCounts.getOrDefault(date, 0) * RECORD_SCORE_PER_MEMBER;
            }
            responses.add(new DailyScoreResponse(toDayCode(date.getDayOfWeek()), score));
        }
        return responses;
    }

    private List<GuildDailyStatResponse> buildDailyStats(Long guildId, GuildScoreService.WeekPeriod period) {
        int activeMemberCount = dashboardRepository.countActiveGuildMembers(guildId);
        Map<LocalDate, Integer> recordCounts = toDailyCountMap(
                dashboardRepository.findDailyRecordedMembers(guildId, period.startDate(), period.endDate())
        );
        Map<LocalDate, Integer> completedQuestCounts = toDailyCountMap(
                dashboardRepository.findDailyCompletedQuests(guildId, period.startDate(), period.endDate())
        );
        Map<LocalDate, Integer> logScores = toDailyScoreMap(
                guildScoreRepository.findDailyScores(guildId, period.startDate(), period.endDate())
        );
        List<GuildDailyStatResponse> responses = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = period.startDate().plusDays(i);
            int recordCount = recordCounts.getOrDefault(date, 0);
            int logScore = logScores.getOrDefault(date, 0);
            int score = logScore == 0 ? recordCount * RECORD_SCORE_PER_MEMBER : logScore;
            int damage = Math.max(0, score - recordCount * RECORD_SCORE_PER_MEMBER);
            responses.add(new GuildDailyStatResponse(
                    date,
                    toDayCode(date.getDayOfWeek()),
                    recordCount,
                    activeMemberCount,
                    guildScoreService.safeRate(recordCount, activeMemberCount),
                    completedQuestCounts.getOrDefault(date, 0),
                    damage,
                    score
            ));
        }
        return responses;
    }

    private GuildScoreService.WeekPeriod resolveAndValidate(LocalDate start, LocalDate end) {
        try {
            GuildScoreService.WeekPeriod period = guildScoreService.resolveWeek(start, end);
            period.validate();
            return period;
        } catch (IllegalArgumentException e) {
            throw new BusinessException(DashboardErrorCode.DASHBOARD_PERIOD_INVALID);
        }
    }

    private Map<LocalDate, Integer> toDailyCountMap(List<DailyCountRow> rows) {
        Map<LocalDate, Integer> map = new HashMap<>();
        for (DailyCountRow row : rows) {
            map.put(row.getStatDate(), value(row.getCountValue()));
        }
        return map;
    }

    private Map<LocalDate, Integer> toDailyScoreMap(List<DailyScoreRow> rows) {
        Map<LocalDate, Integer> map = new HashMap<>();
        for (DailyScoreRow row : rows) {
            map.put(row.getScoreDate(), value(row.getScore()));
        }
        return map;
    }

    private String toDayCode(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "MON";
            case TUESDAY -> "TUE";
            case WEDNESDAY -> "WED";
            case THURSDAY -> "THU";
            case FRIDAY -> "FRI";
            case SATURDAY -> "SAT";
            case SUNDAY -> "SUN";
        };
    }

    private int value(Integer value) {
        return value == null ? 0 : value;
    }

    private record WeeklyMetrics(
            double recordRate,
            int bossDamage,
            int weeklyScore,
            int questCompletedCount,
            int questTotalCount
    ) {
    }
}
