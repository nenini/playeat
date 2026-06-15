package com.nyamnyam.coach.boss.repository.row;

import java.time.LocalDateTime;

public class BossBattleParticipantRow {

    private Long participantId;
    private Long battleId;
    private Long guildId;
    private Long userId;
    private Long guildMemberId;
    private String roleAtStart;
    private String status;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private String snapshotNickname;
    private String snapshotProfileImageUrl;
    private Long snapshotCharacterId;
    private String snapshotCharacterName;
    private Integer snapshotCharacterLevel;

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public Long getBattleId() { return battleId; }
    public void setBattleId(Long battleId) { this.battleId = battleId; }
    public Long getGuildId() { return guildId; }
    public void setGuildId(Long guildId) { this.guildId = guildId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getGuildMemberId() { return guildMemberId; }
    public void setGuildMemberId(Long guildMemberId) { this.guildMemberId = guildMemberId; }
    public String getRoleAtStart() { return roleAtStart; }
    public void setRoleAtStart(String roleAtStart) { this.roleAtStart = roleAtStart; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    public LocalDateTime getLeftAt() { return leftAt; }
    public void setLeftAt(LocalDateTime leftAt) { this.leftAt = leftAt; }
    public String getSnapshotNickname() { return snapshotNickname; }
    public void setSnapshotNickname(String snapshotNickname) { this.snapshotNickname = snapshotNickname; }
    public String getSnapshotProfileImageUrl() { return snapshotProfileImageUrl; }
    public void setSnapshotProfileImageUrl(String snapshotProfileImageUrl) { this.snapshotProfileImageUrl = snapshotProfileImageUrl; }
    public Long getSnapshotCharacterId() { return snapshotCharacterId; }
    public void setSnapshotCharacterId(Long snapshotCharacterId) { this.snapshotCharacterId = snapshotCharacterId; }
    public String getSnapshotCharacterName() { return snapshotCharacterName; }
    public void setSnapshotCharacterName(String snapshotCharacterName) { this.snapshotCharacterName = snapshotCharacterName; }
    public Integer getSnapshotCharacterLevel() { return snapshotCharacterLevel; }
    public void setSnapshotCharacterLevel(Integer snapshotCharacterLevel) { this.snapshotCharacterLevel = snapshotCharacterLevel; }
}
