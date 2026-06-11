package com.nyamnyam.coach.boss.repository.row;

import java.time.LocalDateTime;

public class BossBattleRow {

    private Long battleId;
    private Long guildId;
    private String guildName;
    private Long bossId;
    private Long seasonId;
    private String bossName;
    private String difficulty;
    private String bossImageUrl;
    private String status;
    private Integer maxHp;
    private Integer currentHp;
    private Integer totalDamage;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime endsAt;

    public Long getBattleId() { return battleId; }
    public void setBattleId(Long battleId) { this.battleId = battleId; }
    public Long getGuildId() { return guildId; }
    public void setGuildId(Long guildId) { this.guildId = guildId; }
    public String getGuildName() { return guildName; }
    public void setGuildName(String guildName) { this.guildName = guildName; }
    public Long getBossId() { return bossId; }
    public void setBossId(Long bossId) { this.bossId = bossId; }
    public Long getSeasonId() { return seasonId; }
    public void setSeasonId(Long seasonId) { this.seasonId = seasonId; }
    public String getBossName() { return bossName; }
    public void setBossName(String bossName) { this.bossName = bossName; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getBossImageUrl() { return bossImageUrl; }
    public void setBossImageUrl(String bossImageUrl) { this.bossImageUrl = bossImageUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getMaxHp() { return maxHp; }
    public void setMaxHp(Integer maxHp) { this.maxHp = maxHp; }
    public Integer getCurrentHp() { return currentHp; }
    public void setCurrentHp(Integer currentHp) { this.currentHp = currentHp; }
    public Integer getTotalDamage() { return totalDamage; }
    public void setTotalDamage(Integer totalDamage) { this.totalDamage = totalDamage; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public LocalDateTime getEndsAt() { return endsAt; }
    public void setEndsAt(LocalDateTime endsAt) { this.endsAt = endsAt; }
}
