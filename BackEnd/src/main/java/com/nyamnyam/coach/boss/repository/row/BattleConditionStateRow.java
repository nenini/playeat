package com.nyamnyam.coach.boss.repository.row;

import java.math.BigDecimal;

public class BattleConditionStateRow {

    private Long battleConditionId;
    private Long battleId;
    private String title;
    private String targetType;
    private BigDecimal thresholdValue;
    private Integer targetValue;
    private Integer currentValue;
    private Integer damage;
    private Boolean completed;

    public Long getBattleConditionId() { return battleConditionId; }
    public void setBattleConditionId(Long battleConditionId) { this.battleConditionId = battleConditionId; }
    public Long getBattleId() { return battleId; }
    public void setBattleId(Long battleId) { this.battleId = battleId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public BigDecimal getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(BigDecimal thresholdValue) { this.thresholdValue = thresholdValue; }
    public Integer getTargetValue() { return targetValue; }
    public void setTargetValue(Integer targetValue) { this.targetValue = targetValue; }
    public Integer getCurrentValue() { return currentValue; }
    public void setCurrentValue(Integer currentValue) { this.currentValue = currentValue; }
    public Integer getDamage() { return damage; }
    public void setDamage(Integer damage) { this.damage = damage; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
}
