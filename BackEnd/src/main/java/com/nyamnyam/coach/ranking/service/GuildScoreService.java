package com.nyamnyam.coach.ranking.service;

import com.nyamnyam.coach.boss.entity.BossDifficulty;
import com.nyamnyam.coach.ranking.entity.GuildScoreLog;
import com.nyamnyam.coach.ranking.entity.GuildScoreSourceType;
import com.nyamnyam.coach.ranking.repository.GuildScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class GuildScoreService {

    private final GuildScoreRepository guildScoreRepository;

    public GuildScoreService(GuildScoreRepository guildScoreRepository) {
        this.guildScoreRepository = guildScoreRepository;
    }

    @Transactional
    public void addScoreLog(
            Long guildId,
            Long userId,
            Long battleId,
            GuildScoreSourceType sourceType,
            Long sourceId,
            int score,
            LocalDate scoreDate,
            String description
    ) {
        GuildScoreLog log = new GuildScoreLog();
        log.setGuildId(guildId);
        log.setUserId(userId);
        log.setBattleId(battleId);
        log.setSourceType(sourceType.name());
        log.setSourceId(sourceId);
        log.setScore(score);
        log.setScoreDate(scoreDate);
        log.setDescription(description);
        guildScoreRepository.insertScoreLog(log);
    }

    public int calculateWeeklyScore(
            double recordRate,
            double questCompletionRate,
            double bossProgressRate,
            int clearBonus
    ) {
        return calculateRecordScore(recordRate)
                + calculateQuestScore(questCompletionRate)
                + calculateBossProgressScore(bossProgressRate)
                + clearBonus;
    }

    public int calculateRecordScore(double recordRate) {
        return (int) Math.round(recordRate / 100.0 * 1000);
    }

    public int calculateQuestScore(double questCompletionRate) {
        return (int) Math.round(questCompletionRate / 100.0 * 1000);
    }

    public int calculateBossProgressScore(double bossProgressRate) {
        return (int) Math.round(bossProgressRate / 100.0 * 800);
    }

    public int calculateClearBonus(String difficulty, String battleStatus) {
        if (!isCleared(battleStatus)) {
            return 0;
        }
        if (difficulty == null) {
            return 0;
        }
        return switch (BossDifficulty.valueOf(difficulty)) {
            case EASY -> 300;
            case NORMAL -> 600;
            case HARD -> 1000;
        };
    }

    public boolean isCleared(String battleStatus) {
        return "DEFEATED".equals(battleStatus) || "CLEARED".equals(battleStatus);
    }

    public WeekPeriod resolveWeek(LocalDate weekStartDate, LocalDate weekEndDate) {
        if (weekStartDate == null && weekEndDate == null) {
            LocalDate today = LocalDate.now();
            LocalDate start = today.with(DayOfWeek.MONDAY);
            return new WeekPeriod(start, start.plusDays(6));
        }
        if (weekStartDate == null || weekEndDate == null) {
            throw new IllegalArgumentException("Both weekStartDate and weekEndDate are required.");
        }
        return new WeekPeriod(weekStartDate, weekEndDate);
    }

    public int calculateElapsedDaysInWeek(LocalDate weekStartDate, LocalDate weekEndDate) {
        LocalDate today = LocalDate.now();
        LocalDate effectiveEnd = weekEndDate.isAfter(today) ? today : weekEndDate;
        if (effectiveEnd.isBefore(weekStartDate)) {
            return 0;
        }
        return (int) (effectiveEnd.toEpochDay() - weekStartDate.toEpochDay()) + 1;
    }

    public double safeRate(int numerator, int denominator) {
        if (denominator <= 0) {
            return 0.0;
        }
        return roundToOneDecimal(Math.min((double) numerator / denominator * 100.0, 100.0));
    }

    public double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    public record WeekPeriod(LocalDate startDate, LocalDate endDate) {
        public void validate() {
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("weekStartDate must be before weekEndDate.");
            }
            if (endDate.toEpochDay() - startDate.toEpochDay() > 13) {
                throw new IllegalArgumentException("Period must be 14 days or less.");
            }
        }
    }
}
