package com.nyamnyam.coach.boss.entity;

import java.time.LocalDateTime;

public class RewardClaim {

    private Long rewardClaimId;
    private Long userId;
    private String sourceType;
    private Long sourceId;
    private Integer xpAmount;
    private Integer guildPoint;
    private Integer coinAmount;
    private LocalDateTime claimedAt;

    public Long getRewardClaimId() { return rewardClaimId; }
    public void setRewardClaimId(Long rewardClaimId) { this.rewardClaimId = rewardClaimId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
    public Integer getXpAmount() { return xpAmount; }
    public void setXpAmount(Integer xpAmount) { this.xpAmount = xpAmount; }
    public Integer getGuildPoint() { return guildPoint; }
    public void setGuildPoint(Integer guildPoint) { this.guildPoint = guildPoint; }
    public Integer getCoinAmount() { return coinAmount; }
    public void setCoinAmount(Integer coinAmount) { this.coinAmount = coinAmount; }
    public LocalDateTime getClaimedAt() { return claimedAt; }
    public void setClaimedAt(LocalDateTime claimedAt) { this.claimedAt = claimedAt; }
}
