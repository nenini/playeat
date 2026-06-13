package com.nyamnyam.coach.boss.entity;

import java.time.LocalDateTime;

public class BossBattle {

    private Long battleId;
    private Long guildId;
    private Long bossId;
    private Long seasonId;
    private String status;
    private Integer maxHp;
    private Integer currentHp;
    private Integer totalDamage;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

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

    public Long getBossId() {
        return bossId;
    }

    public void setBossId(Long bossId) {
        this.bossId = bossId;
    }

    public Long getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(Integer maxHp) {
        this.maxHp = maxHp;
    }

    public Integer getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(Integer currentHp) {
        this.currentHp = currentHp;
    }

    public Integer getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(Integer totalDamage) {
        this.totalDamage = totalDamage;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }
}
