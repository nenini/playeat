package com.nyamnyam.coach.dashboard.repository.row;

public class BattleDashboardRow {

    private Long battleId;
    private Long guildId;
    private String bossName;
    private String difficulty;
    private String status;
    private Integer maxHp;
    private Integer currentHp;
    private Integer totalDamage;

    public Long getBattleId() { return battleId; }
    public void setBattleId(Long battleId) { this.battleId = battleId; }
    public Long getGuildId() { return guildId; }
    public void setGuildId(Long guildId) { this.guildId = guildId; }
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
}
