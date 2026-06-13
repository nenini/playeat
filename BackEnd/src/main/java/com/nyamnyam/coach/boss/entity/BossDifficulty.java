package com.nyamnyam.coach.boss.entity;

public enum BossDifficulty {
    EASY(500, 150, 0.80, 0.20),
    NORMAL(1000, 250, 0.70, 0.30),
    HARD(1800, 400, 0.60, 0.40);

    private final int baseHp;
    private final int hpPerMember;
    private final double personalDamageRatio;
    private final double commonConditionDamageRatio;

    BossDifficulty(
            int baseHp,
            int hpPerMember,
            double personalDamageRatio,
            double commonConditionDamageRatio
    ) {
        this.baseHp = baseHp;
        this.hpPerMember = hpPerMember;
        this.personalDamageRatio = personalDamageRatio;
        this.commonConditionDamageRatio = commonConditionDamageRatio;
    }

    public int calculateMaxHp(int activeMemberCount) {
        return baseHp + activeMemberCount * hpPerMember;
    }

    public int calculatePersonalTotalDamage(int maxHp) {
        return (int) Math.round(maxHp * personalDamageRatio);
    }

    public int calculateCommonConditionTotalDamage(int maxHp) {
        return (int) Math.round(maxHp * commonConditionDamageRatio);
    }

    public int getBaseHp() {
        return baseHp;
    }

    public int getHpPerMember() {
        return hpPerMember;
    }

    public double getPersonalDamageRatio() {
        return personalDamageRatio;
    }

    public double getCommonConditionDamageRatio() {
        return commonConditionDamageRatio;
    }

    public static BossDifficulty from(String difficulty) {
        return BossDifficulty.valueOf(difficulty);
    }
}
