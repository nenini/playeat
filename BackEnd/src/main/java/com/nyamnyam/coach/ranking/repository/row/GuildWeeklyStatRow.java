package com.nyamnyam.coach.ranking.repository.row;

public class GuildWeeklyStatRow {

    private Long guildId;
    private String guildName;
    private Integer activeMemberCount;
    private Integer recordedMemberDateCount;
    private Integer questTotalCount;
    private Integer questCompletedCount;
    private Integer bossDamage;
    private Integer bossMaxHp;
    private Integer bossCurrentHp;
    private String battleStatus;
    private String difficulty;

    public Long getGuildId() { return guildId; }
    public void setGuildId(Long guildId) { this.guildId = guildId; }
    public String getGuildName() { return guildName; }
    public void setGuildName(String guildName) { this.guildName = guildName; }
    public Integer getActiveMemberCount() { return activeMemberCount; }
    public void setActiveMemberCount(Integer activeMemberCount) { this.activeMemberCount = activeMemberCount; }
    public Integer getRecordedMemberDateCount() { return recordedMemberDateCount; }
    public void setRecordedMemberDateCount(Integer recordedMemberDateCount) { this.recordedMemberDateCount = recordedMemberDateCount; }
    public Integer getQuestTotalCount() { return questTotalCount; }
    public void setQuestTotalCount(Integer questTotalCount) { this.questTotalCount = questTotalCount; }
    public Integer getQuestCompletedCount() { return questCompletedCount; }
    public void setQuestCompletedCount(Integer questCompletedCount) { this.questCompletedCount = questCompletedCount; }
    public Integer getBossDamage() { return bossDamage; }
    public void setBossDamage(Integer bossDamage) { this.bossDamage = bossDamage; }
    public Integer getBossMaxHp() { return bossMaxHp; }
    public void setBossMaxHp(Integer bossMaxHp) { this.bossMaxHp = bossMaxHp; }
    public Integer getBossCurrentHp() { return bossCurrentHp; }
    public void setBossCurrentHp(Integer bossCurrentHp) { this.bossCurrentHp = bossCurrentHp; }
    public String getBattleStatus() { return battleStatus; }
    public void setBattleStatus(String battleStatus) { this.battleStatus = battleStatus; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}
