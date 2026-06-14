package com.nyamnyam.coach.ranking.repository.row;

public class BossInfoRow {

    private Long bossId;
    private String bossName;
    private String difficulty;

    public Long getBossId() { return bossId; }
    public void setBossId(Long bossId) { this.bossId = bossId; }
    public String getBossName() { return bossName; }
    public void setBossName(String bossName) { this.bossName = bossName; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}
