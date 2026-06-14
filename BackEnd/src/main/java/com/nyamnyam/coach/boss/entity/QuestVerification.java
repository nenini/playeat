package com.nyamnyam.coach.boss.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class QuestVerification {

    private Long verificationId;
    private Long questId;
    private Long userId;
    private Long battleId;
    private Long summaryId;
    private Long dietId;
    private String questType;
    private Boolean verified;
    private Integer damageAmount;
    private String message;
    private LocalDate verifiedDate;
    private LocalDateTime verifiedAt;

    public Long getVerificationId() { return verificationId; }
    public void setVerificationId(Long verificationId) { this.verificationId = verificationId; }
    public Long getQuestId() { return questId; }
    public void setQuestId(Long questId) { this.questId = questId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBattleId() { return battleId; }
    public void setBattleId(Long battleId) { this.battleId = battleId; }
    public Long getSummaryId() { return summaryId; }
    public void setSummaryId(Long summaryId) { this.summaryId = summaryId; }
    public Long getDietId() { return dietId; }
    public void setDietId(Long dietId) { this.dietId = dietId; }
    public String getQuestType() { return questType; }
    public void setQuestType(String questType) { this.questType = questType; }
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    public Integer getDamageAmount() { return damageAmount; }
    public void setDamageAmount(Integer damageAmount) { this.damageAmount = damageAmount; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDate getVerifiedDate() { return verifiedDate; }
    public void setVerifiedDate(LocalDate verifiedDate) { this.verifiedDate = verifiedDate; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
}
