package com.nyamnyam.coach.boss.entity;

import java.math.BigDecimal;

public class BossBattleCondition {

    private Long battleConditionId;
    private Long battleId;
    private Long conditionId;
    private Long conditionTemplateId;
    private String title;
    private String description;
    private String targetType;
    private String conditionCategory;
    private String metricType;
    private String comparisonType;
    private String aggregationType;
    private String evaluationScope;
    private BigDecimal thresholdValue;
    private BigDecimal thresholdMinValue;
    private BigDecimal thresholdMaxValue;
    private String thresholdUnit;
    private Integer targetValue;
    private Integer requiredDays;
    private Integer currentValue;
    private Integer damage;
    private Boolean completed;
    private Boolean requiredForClear;
    private Boolean verificationSupported;
    private String unit;
    private Integer sortOrder;

    public Long getBattleConditionId() {
        return battleConditionId;
    }

    public void setBattleConditionId(Long battleConditionId) {
        this.battleConditionId = battleConditionId;
    }

    public Long getBattleId() {
        return battleId;
    }

    public void setBattleId(Long battleId) {
        this.battleId = battleId;
    }

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public Long getConditionTemplateId() { return conditionTemplateId; }
    public void setConditionTemplateId(Long conditionTemplateId) { this.conditionTemplateId = conditionTemplateId; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getConditionCategory() { return conditionCategory; }
    public void setConditionCategory(String conditionCategory) { this.conditionCategory = conditionCategory; }
    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }
    public String getComparisonType() { return comparisonType; }
    public void setComparisonType(String comparisonType) { this.comparisonType = comparisonType; }
    public String getAggregationType() { return aggregationType; }
    public void setAggregationType(String aggregationType) { this.aggregationType = aggregationType; }
    public String getEvaluationScope() { return evaluationScope; }
    public void setEvaluationScope(String evaluationScope) { this.evaluationScope = evaluationScope; }

    public BigDecimal getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(BigDecimal thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public BigDecimal getThresholdMinValue() { return thresholdMinValue; }
    public void setThresholdMinValue(BigDecimal thresholdMinValue) { this.thresholdMinValue = thresholdMinValue; }
    public BigDecimal getThresholdMaxValue() { return thresholdMaxValue; }
    public void setThresholdMaxValue(BigDecimal thresholdMaxValue) { this.thresholdMaxValue = thresholdMaxValue; }

    public String getThresholdUnit() {
        return thresholdUnit;
    }

    public void setThresholdUnit(String thresholdUnit) {
        this.thresholdUnit = thresholdUnit;
    }

    public Integer getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Integer targetValue) {
        this.targetValue = targetValue;
    }

    public Integer getRequiredDays() {
        return requiredDays;
    }

    public void setRequiredDays(Integer requiredDays) {
        this.requiredDays = requiredDays;
    }

    public Integer getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
    }

    public Integer getDamage() {
        return damage;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getRequiredForClear() { return requiredForClear; }
    public void setRequiredForClear(Boolean requiredForClear) { this.requiredForClear = requiredForClear; }
    public Boolean getVerificationSupported() { return verificationSupported; }
    public void setVerificationSupported(Boolean verificationSupported) { this.verificationSupported = verificationSupported; }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
