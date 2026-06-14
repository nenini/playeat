package com.nyamnyam.coach.ranking.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GuildScoreLog {

    private Long scoreLogId;
    private Long guildId;
    private Long userId;
    private Long battleId;
    private String sourceType;
    private Long sourceId;
    private Integer score;
    private LocalDate scoreDate;
    private String description;
    private LocalDateTime createdAt;

    public Long getScoreLogId() { return scoreLogId; }
    public void setScoreLogId(Long scoreLogId) { this.scoreLogId = scoreLogId; }
    public Long getGuildId() { return guildId; }
    public void setGuildId(Long guildId) { this.guildId = guildId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBattleId() { return battleId; }
    public void setBattleId(Long battleId) { this.battleId = battleId; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public LocalDate getScoreDate() { return scoreDate; }
    public void setScoreDate(LocalDate scoreDate) { this.scoreDate = scoreDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
