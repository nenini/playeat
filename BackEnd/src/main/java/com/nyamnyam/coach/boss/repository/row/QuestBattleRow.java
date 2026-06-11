package com.nyamnyam.coach.boss.repository.row;

public class QuestBattleRow {

    private Long battleId;
    private Long guildId;
    private String status;
    private Integer maxHp;

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
}
