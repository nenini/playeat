package com.nyamnyam.coach.boss.service;

public record ConditionEvaluationResult(
        boolean satisfied,
        int currentValue,
        Long dietId
) {
}
