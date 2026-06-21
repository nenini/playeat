package com.nyamnyam.coach.quest.service;

import com.nyamnyam.coach.boss.repository.row.BattleConditionStateRow;
import com.nyamnyam.coach.boss.repository.row.BattleStateRow;
import com.nyamnyam.coach.quest.repository.QuestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConditionEvaluationServiceTest {

    @Mock
    private QuestRepository questRepository;

    @Test
    void totalCountUsesDietRowAggregationForBattlePeriod() {
        ConditionEvaluationService service = new ConditionEvaluationService(questRepository);
        BattleStateRow battle = battle();
        BattleConditionStateRow condition = condition("TOTAL_COUNT");
        LocalDate today = LocalDate.of(2026, 6, 21);
        LocalDateTime endAt = LocalDate.of(2026, 6, 22).atStartOfDay();
        when(questRepository.countGuildBattleSatisfiedDiets(
                1L,
                battle.getStartedAt(),
                endAt,
                "SUGAR",
                "LESS_THAN_OR_EQUAL",
                new BigDecimal("20"),
                null,
                null
        )).thenReturn(3);

        int currentValue = service.evaluateGuildBattleCondition(battle, condition, today);

        assertThat(currentValue).isEqualTo(3);
        verify(questRepository).countGuildBattleSatisfiedDiets(
                1L,
                battle.getStartedAt(),
                endAt,
                "SUGAR",
                "LESS_THAN_OR_EQUAL",
                new BigDecimal("20"),
                null,
                null
        );
    }

    @Test
    void daysSatisfiedKeepsExistingDailyAggregation() {
        ConditionEvaluationService service = new ConditionEvaluationService(questRepository);
        BattleStateRow battle = battle();
        BattleConditionStateRow condition = condition("DAYS_SATISFIED");
        LocalDate today = LocalDate.of(2026, 6, 21);
        when(questRepository.countGuildBattleSatisfiedMemberDates(
                1L,
                LocalDate.of(2026, 6, 15),
                today,
                "SUGAR",
                "LESS_THAN_OR_EQUAL",
                new BigDecimal("20"),
                null,
                null
        )).thenReturn(2);

        assertThat(service.evaluateGuildBattleCondition(battle, condition, today)).isEqualTo(2);
    }

    private BattleStateRow battle() {
        BattleStateRow battle = new BattleStateRow();
        battle.setBattleId(1L);
        battle.setStartedAt(LocalDateTime.of(2026, 6, 15, 10, 30));
        return battle;
    }

    private BattleConditionStateRow condition(String aggregationType) {
        BattleConditionStateRow condition = new BattleConditionStateRow();
        condition.setEvaluationScope("GUILD_BATTLE_PERIOD");
        condition.setAggregationType(aggregationType);
        condition.setMetricType("SUGAR");
        condition.setComparisonType("LESS_THAN_OR_EQUAL");
        condition.setThresholdValue(new BigDecimal("20"));
        return condition;
    }
}
