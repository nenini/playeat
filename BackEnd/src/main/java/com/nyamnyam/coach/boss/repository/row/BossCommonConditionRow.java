package com.nyamnyam.coach.boss.repository.row;

import java.math.BigDecimal;

public class BossCommonConditionRow {

    private Long conditionId;
    private Long conditionTemplateId;
    private Long seasonId;
    private Long bossId;
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
    private String unit;
    private Integer sortOrder;
    private Boolean requiredForClear;
    private Boolean verificationSupported;

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public Long getConditionTemplateId() { return conditionTemplateId; }
    public void setConditionTemplateId(Long conditionTemplateId) { this.conditionTemplateId = conditionTemplateId; }

    public Long getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
    }

    public Long getBossId() {
        return bossId;
    }

    public void setBossId(Long bossId) {
        this.bossId = bossId;
    }

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

    public Boolean getRequiredForClear() { return requiredForClear; }
    public void setRequiredForClear(Boolean requiredForClear) { this.requiredForClear = requiredForClear; }
    public Boolean getVerificationSupported() { return verificationSupported; }
    public void setVerificationSupported(Boolean verificationSupported) { this.verificationSupported = verificationSupported; }
}
