package com.nyamnyam.coach.ranking.service;

import com.nyamnyam.coach.ranking.repository.GuildScoreRepository;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GuildScoreServiceTest {

    private final GuildScoreService guildScoreService = new GuildScoreService(mock(GuildScoreRepository.class));

    @Test
    void calculateWeeklyScore() {
        int weeklyScore = guildScoreService.calculateWeeklyScore(92.0, 70.8, 54.0, 300);

        assertThat(weeklyScore).isEqualTo(2360);
    }

    @Test
    void calculateClearBonusByDifficulty() {
        assertThat(guildScoreService.calculateClearBonus("EASY", "DEFEATED")).isEqualTo(300);
        assertThat(guildScoreService.calculateClearBonus("NORMAL", "DEFEATED")).isEqualTo(600);
        assertThat(guildScoreService.calculateClearBonus("HARD", "DEFEATED")).isEqualTo(1000);
        assertThat(guildScoreService.calculateClearBonus("HARD", "IN_PROGRESS")).isZero();
    }

    @Test
    void safeRateReturnsZeroWhenDenominatorIsZero() {
        assertThat(guildScoreService.safeRate(10, 0)).isZero();
        assertThat(guildScoreService.safeRate(22, 24)).isEqualTo(91.7);
    }

    @Test
    void resolveCurrentWeekStartsMondayAndEndsSunday() {
        GuildScoreService.WeekPeriod period = guildScoreService.resolveWeek(null, null);

        assertThat(period.startDate().getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(period.endDate()).isEqualTo(period.startDate().plusDays(6));
    }

    @Test
    void calculateElapsedDaysExcludesFutureDates() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.with(DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);

        int elapsedDays = guildScoreService.calculateElapsedDaysInWeek(start, end);

        assertThat(elapsedDays).isBetween(1, 7);
    }
}
