package com.nyamnyam.coach.boss.repository.row;

import java.time.LocalDateTime;

public class BattleStateRow {

    private Long battleId;
    private Long guildId;
    private Long bossId;
    private String bossName;
    private String difficulty;
    private String status;
    private Integer maxHp;
    private Integer currentHp;
    private Integer totalDamage;
    private Integer rewardExp;
    private Integer rewardCoin;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public Long getBattleId() { return battleId; }
    public void setBattleId(Long battleId) { this.battleId = battleId; }
    public Long getGuildId() { return guildId; }
    public void setGuildId(Long guildId) { this.guildId = guildId; }
    public Long getBossId() { return bossId; }
    public void setBossId(Long bossId) { this.bossId = bossId; }
    public String getBossName() { return bossName; }
    public void setBossName(String bossName) { this.bossName = bossName; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getMaxHp() { return maxHp; }
    public void setMaxHp(Integer maxHp) { this.maxHp = maxHp; }
    public Integer getCurrentHp() { return currentHp; }
    public void setCurrentHp(Integer currentHp) { this.currentHp = currentHp; }
    public Integer getTotalDamage() { return totalDamage; }
    public void setTotalDamage(Integer totalDamage) { this.totalDamage = totalDamage; }
    public Integer getRewardExp() { return rewardExp; }
    public void setRewardExp(Integer rewardExp) { this.rewardExp = rewardExp; }
    public Integer getRewardCoin() { return rewardCoin; }
    public void setRewardCoin(Integer rewardCoin) { this.rewardCoin = rewardCoin; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
}
