package com.nyamnyam.coach.boss.repository.row;

import java.math.BigDecimal;

public class BossBattleConditionRow {

    private Long battleConditionId;
    private Long battleId;
    private Long conditionId;
    private String title;
    private String description;
    private String targetType;
    private BigDecimal thresholdValue;
    private String thresholdUnit;
    private Integer targetValue;
    private Integer requiredDays;
    private Integer currentValue;
    private String unit;
    private Boolean completed;
    private Integer sortOrder;

    public Long getBattleConditionId() { return battleConditionId; }
    public void setBattleConditionId(Long battleConditionId) { this.battleConditionId = battleConditionId; }
    public Long getBattleId() { return battleId; }
    public void setBattleId(Long battleId) { this.battleId = battleId; }
    public Long getConditionId() { return conditionId; }
    public void setConditionId(Long conditionId) { this.conditionId = conditionId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public BigDecimal getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(BigDecimal thresholdValue) { this.thresholdValue = thresholdValue; }
    public String getThresholdUnit() { return thresholdUnit; }
    public void setThresholdUnit(String thresholdUnit) { this.thresholdUnit = thresholdUnit; }
    public Integer getTargetValue() { return targetValue; }
    public void setTargetValue(Integer targetValue) { this.targetValue = targetValue; }
    public Integer getRequiredDays() { return requiredDays; }
    public void setRequiredDays(Integer requiredDays) { this.requiredDays = requiredDays; }
    public Integer getCurrentValue() { return currentValue; }
    public void setCurrentValue(Integer currentValue) { this.currentValue = currentValue; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
