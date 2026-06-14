package com.nyamnyam.coach.ranking.repository.row;

import java.time.LocalDateTime;

public class BossRankingRow {

    private Long guildId;
    private String guildName;
    private String status;
    private Integer maxHp;
    private Integer currentHp;
    private Integer totalDamage;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public Long getGuildId() { return guildId; }
    public void setGuildId(Long guildId) { this.guildId = guildId; }
    public String getGuildName() { return guildName; }
    public void setGuildName(String guildName) { this.guildName = guildName; }
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
}
