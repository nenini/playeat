package com.nyamnyam.coach.quest.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuestTemplate {

    private Long templateId;
    private String title;
    private String description;
    private String questType;
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
    private String unit;
    private Integer damage;
    private Integer rewardExp;
    private Integer rewardCoin;
    private String difficulty;
    private Boolean active;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getQuestType() { return questType; }
    public void setQuestType(String questType) { this.questType = questType; }
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
    public BigDecimal getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(BigDecimal thresholdValue) { this.thresholdValue = thresholdValue; }
    public BigDecimal getThresholdMinValue() { return thresholdMinValue; }
    public void setThresholdMinValue(BigDecimal thresholdMinValue) { this.thresholdMinValue = thresholdMinValue; }
    public BigDecimal getThresholdMaxValue() { return thresholdMaxValue; }
    public void setThresholdMaxValue(BigDecimal thresholdMaxValue) { this.thresholdMaxValue = thresholdMaxValue; }
    public String getThresholdUnit() { return thresholdUnit; }
    public void setThresholdUnit(String thresholdUnit) { this.thresholdUnit = thresholdUnit; }
    public Integer getTargetValue() { return targetValue; }
    public void setTargetValue(Integer targetValue) { this.targetValue = targetValue; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Integer getDamage() { return damage; }
    public void setDamage(Integer damage) { this.damage = damage; }
    public Integer getRewardExp() { return rewardExp; }
    public void setRewardExp(Integer rewardExp) { this.rewardExp = rewardExp; }
    public Integer getRewardCoin() { return rewardCoin; }
    public void setRewardCoin(Integer rewardCoin) { this.rewardCoin = rewardCoin; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
