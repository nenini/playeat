package com.nyamnyam.coach.boss.repository.row;

import java.time.LocalDateTime;

public class QuestContributionRow {

    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String characterName;
    private Integer characterLevel;
    private Integer totalQuestCount;
    private Integer completedQuestCount;
    private Integer totalDamage;
    private Integer expectedDamage;
    private String participantStatus;
    private LocalDateTime leftAt;
    private Boolean isMe;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public Integer getCharacterLevel() {
        return characterLevel;
    }

    public void setCharacterLevel(Integer characterLevel) {
        this.characterLevel = characterLevel;
    }

    public Integer getTotalQuestCount() {
        return totalQuestCount;
    }

    public void setTotalQuestCount(Integer totalQuestCount) {
        this.totalQuestCount = totalQuestCount;
    }

    public Integer getCompletedQuestCount() {
        return completedQuestCount;
    }

    public void setCompletedQuestCount(Integer completedQuestCount) {
        this.completedQuestCount = completedQuestCount;
    }

    public Integer getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(Integer totalDamage) {
        this.totalDamage = totalDamage;
    }

    public Integer getExpectedDamage() {
        return expectedDamage;
    }

    public void setExpectedDamage(Integer expectedDamage) {
        this.expectedDamage = expectedDamage;
    }

    public String getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(String participantStatus) {
        this.participantStatus = participantStatus;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(LocalDateTime leftAt) {
        this.leftAt = leftAt;
    }

    public Boolean getIsMe() {
        return isMe;
    }

    public void setIsMe(Boolean isMe) {
        this.isMe = isMe;
    }
}
