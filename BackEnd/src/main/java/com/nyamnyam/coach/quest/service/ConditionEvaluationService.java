package com.nyamnyam.coach.quest.service;

import com.nyamnyam.coach.boss.service.ConditionEvaluationResult;
import com.nyamnyam.coach.quest.entity.Quest;
import com.nyamnyam.coach.quest.entity.QuestAggregationType;
import com.nyamnyam.coach.quest.entity.QuestComparisonType;
import com.nyamnyam.coach.quest.entity.QuestConditionCategory;
import com.nyamnyam.coach.quest.entity.QuestEvaluationScope;
import com.nyamnyam.coach.quest.entity.QuestMetricType;
import com.nyamnyam.coach.quest.repository.QuestRepository;
import com.nyamnyam.coach.boss.repository.row.BattleConditionStateRow;
import com.nyamnyam.coach.boss.repository.row.BattleStateRow;
import com.nyamnyam.coach.quest.repository.row.DietVerificationRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.QuestErrorCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ConditionEvaluationService {

    private final QuestRepository questRepository;

    public ConditionEvaluationService(QuestRepository questRepository) {
        this.questRepository = questRepository;
    }

    public ConditionEvaluationResult evaluateQuest(Quest quest, Long userId, LocalDate date) {
        QuestEvaluationScope scope = parse(QuestEvaluationScope.class, quest.getEvaluationScope());
        if (scope != QuestEvaluationScope.USER_DAILY) {
            throw new BusinessException(QuestErrorCode.QUEST_UNSUPPORTED_TYPE);
        }

        LocalDateTime startAt = date.atStartOfDay();
        LocalDateTime endAt = date.plusDays(1).atStartOfDay();
        DietVerificationRow diet = questRepository.findTodayDietForVerification(userId, startAt, endAt).orElse(null);
        if (QuestConditionCategory.NUTRITION.name().equals(quest.getConditionCategory()) && diet == null) {
            return new ConditionEvaluationResult(false, 0, null);
        }
        int currentValue = evaluateUserDaily(
                userId,
                startAt,
                endAt,
                quest.getConditionCategory(),
                quest.getMetricType(),
                quest.getComparisonType(),
                quest.getThresholdValue(),
                quest.getThresholdMinValue(),
                quest.getThresholdMaxValue()
        );
        int targetValue = defaultValue(quest.getTargetValue(), 1);
        return new ConditionEvaluationResult(currentValue >= targetValue, currentValue, diet == null ? null : diet.getDietId());
    }

    public int evaluateGuildBattleCondition(BattleStateRow battle, BattleConditionStateRow condition, LocalDate today) {
        QuestEvaluationScope scope = parse(QuestEvaluationScope.class, condition.getEvaluationScope());
        QuestAggregationType aggregationType = parse(QuestAggregationType.class, condition.getAggregationType());
        if (scope != QuestEvaluationScope.GUILD_BATTLE_PERIOD) {
            throw new BusinessException(QuestErrorCode.QUEST_UNSUPPORTED_TYPE);
        }

        if (aggregationType == QuestAggregationType.TOTAL_COUNT) {
            LocalDateTime startAt = battle.getStartedAt() == null
                    ? today.atStartOfDay()
                    : battle.getStartedAt();
            LocalDateTime endAt = today.plusDays(1).atStartOfDay();
            return questRepository.countGuildBattleSatisfiedDiets(
                    battle.getBattleId(),
                    startAt,
                    endAt,
                    condition.getMetricType(),
                    condition.getComparisonType(),
                    condition.getThresholdValue(),
                    condition.getThresholdMinValue(),
                    condition.getThresholdMaxValue()
            );
        }

        if (aggregationType != QuestAggregationType.DAYS_SATISFIED) {
            throw new BusinessException(QuestErrorCode.QUEST_UNSUPPORTED_TYPE);
        }
        LocalDate startDate = battle.getStartedAt() == null ? today : battle.getStartedAt().toLocalDate();
        return questRepository.countGuildBattleSatisfiedMemberDates(
                battle.getBattleId(),
                startDate,
                today,
                condition.getMetricType(),
                condition.getComparisonType(),
                condition.getThresholdValue(),
                condition.getThresholdMinValue(),
                condition.getThresholdMaxValue()
        );
    }

    private int evaluateUserDaily(
            Long userId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String conditionCategoryValue,
            String metricTypeValue,
            String comparisonTypeValue,
            BigDecimal thresholdValue,
            BigDecimal thresholdMinValue,
            BigDecimal thresholdMaxValue
    ) {
        QuestConditionCategory conditionCategory = parse(QuestConditionCategory.class, conditionCategoryValue);
        QuestMetricType metricType = parse(QuestMetricType.class, metricTypeValue);
        QuestComparisonType comparisonType = parse(QuestComparisonType.class, comparisonTypeValue);

        if (conditionCategory == QuestConditionCategory.DIET_RECORD
                && metricType == QuestMetricType.DIET_RECORD_COUNT) {
            int count = questRepository.countDietsByUserAndDate(userId, startAt, endAt);
            return matches(BigDecimal.valueOf(count), comparisonType, thresholdValue, thresholdMinValue, thresholdMaxValue) ? 1 : 0;
        }

        if (conditionCategory == QuestConditionCategory.MEAL_PATTERN) {
            boolean exists = questRepository.existsDietByUserDateMealType(userId, startAt, endAt, metricType.name());
            boolean satisfied = comparisonType == QuestComparisonType.NOT_EXISTS ? !exists : exists;
            return satisfied ? 1 : 0;
        }

        if (conditionCategory == QuestConditionCategory.NUTRITION) {
            BigDecimal value = questRepository.sumDailyNutritionMetric(userId, startAt, endAt, metricType.name());
            return matches(value == null ? BigDecimal.ZERO : value, comparisonType, thresholdValue, thresholdMinValue, thresholdMaxValue) ? 1 : 0;
        }

        throw new BusinessException(QuestErrorCode.QUEST_UNSUPPORTED_TYPE);
    }

    private boolean matches(
            BigDecimal value,
            QuestComparisonType comparisonType,
            BigDecimal thresholdValue,
            BigDecimal thresholdMinValue,
            BigDecimal thresholdMaxValue
    ) {
        return switch (comparisonType) {
            case GREATER_THAN_OR_EQUAL -> thresholdValue != null && value.compareTo(thresholdValue) >= 0;
            case LESS_THAN_OR_EQUAL -> thresholdValue != null && value.compareTo(thresholdValue) <= 0;
            case BETWEEN -> thresholdMinValue != null
                    && thresholdMaxValue != null
                    && value.compareTo(thresholdMinValue) >= 0
                    && value.compareTo(thresholdMaxValue) <= 0;
            case EXISTS -> value.compareTo(BigDecimal.ZERO) > 0;
            case NOT_EXISTS -> value.compareTo(BigDecimal.ZERO) <= 0;
        };
    }

    private <T extends Enum<T>> T parse(Class<T> type, String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(QuestErrorCode.QUEST_UNSUPPORTED_TYPE);
        }
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(QuestErrorCode.QUEST_UNSUPPORTED_TYPE);
        }
    }

    private int defaultValue(Integer value, int fallback) {
        return value == null ? fallback : value;
    }
}
