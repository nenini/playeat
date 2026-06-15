package com.nyamnyam.coach.quest.repository.row;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuestRow {

    private Long questId;
    private Long battleId;
    private Long guildId;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private Long characterId;
    private String characterName;
    private Integer characterLevel;
    private Long questTemplateId;
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
    private Integer currentValue;
    private String unit;
    private Integer damage;
    private Integer rewardExp;
    private Integer rewardCoin;
    private String status;
    private String sourceType;
    private String participantStatus;
    private LocalDateTime participantLeftAt;
    private Boolean isMe;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime rewardedAt;

    public Long getQuestId() {
        return questId;
    }

    public void setQuestId(Long questId) {
        this.questId = questId;
    }

    public Long getBattleId() {
        return battleId;
    }

    public void setBattleId(Long battleId) {
        this.battleId = battleId;
    }

    public Long getGuildId() {
        return guildId;
    }

    public void setGuildId(Long guildId) {
        this.guildId = guildId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public Integer getCharacterLevel() {
        return characterLevel;
    }

    public void setCharacterLevel(Integer characterLevel) {
        this.characterLevel = characterLevel;
    }

    public Long getQuestTemplateId() {
        return questTemplateId;
    }

    public void setQuestTemplateId(Long questTemplateId) {
        this.questTemplateId = questTemplateId;
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

    public String getQuestType() {
        return questType;
    }

    public void setQuestType(String questType) {
        this.questType = questType;
    }

    public String getConditionCategory() {
        return conditionCategory;
    }

    public void setConditionCategory(String conditionCategory) {
        this.conditionCategory = conditionCategory;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public String getComparisonType() {
        return comparisonType;
    }

    public void setComparisonType(String comparisonType) {
        this.comparisonType = comparisonType;
    }

    public String getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(String aggregationType) {
        this.aggregationType = aggregationType;
    }

    public String getEvaluationScope() {
        return evaluationScope;
    }

    public void setEvaluationScope(String evaluationScope) {
        this.evaluationScope = evaluationScope;
    }

    public BigDecimal getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(BigDecimal thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public BigDecimal getThresholdMinValue() {
        return thresholdMinValue;
    }

    public void setThresholdMinValue(BigDecimal thresholdMinValue) {
        this.thresholdMinValue = thresholdMinValue;
    }

    public BigDecimal getThresholdMaxValue() {
        return thresholdMaxValue;
    }

    public void setThresholdMaxValue(BigDecimal thresholdMaxValue) {
        this.thresholdMaxValue = thresholdMaxValue;
    }

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

    public Integer getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getDamage() {
        return damage;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public Integer getRewardExp() {
        return rewardExp;
    }

    public void setRewardExp(Integer rewardExp) {
        this.rewardExp = rewardExp;
    }

    public Integer getRewardCoin() {
        return rewardCoin;
    }

    public void setRewardCoin(Integer rewardCoin) {
        this.rewardCoin = rewardCoin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(String participantStatus) {
        this.participantStatus = participantStatus;
    }

    public LocalDateTime getParticipantLeftAt() {
        return participantLeftAt;
    }

    public void setParticipantLeftAt(LocalDateTime participantLeftAt) {
        this.participantLeftAt = participantLeftAt;
    }

    public Boolean getIsMe() {
        return isMe;
    }

    public void setIsMe(Boolean isMe) {
        this.isMe = isMe;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getRewardedAt() {
        return rewardedAt;
    }

    public void setRewardedAt(LocalDateTime rewardedAt) {
        this.rewardedAt = rewardedAt;
    }
}
