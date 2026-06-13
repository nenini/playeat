package com.nyamnyam.coach.boss.repository.row;

import java.time.LocalDateTime;

public class BossBattleDamageLogRow {

    private Long damageLogId;
    private Long battleId;
    private Long userId;
    private String nickname;
    private Integer damage;
    private String sourceType;
    private Long sourceId;
    private String description;
    private LocalDateTime createdAt;

    public Long getDamageLogId() { return damageLogId; }
    public void setDamageLogId(Long damageLogId) { this.damageLogId = damageLogId; }
    public Long getBattleId() { return battleId; }
    public void setBattleId(Long battleId) { this.battleId = battleId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Integer getDamage() { return damage; }
    public void setDamage(Integer damage) { this.damage = damage; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
