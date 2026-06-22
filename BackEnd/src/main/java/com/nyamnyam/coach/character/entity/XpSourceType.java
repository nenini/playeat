package com.nyamnyam.coach.character.entity;

import java.util.Arrays;

public enum XpSourceType {
    DIET,
    BOSS,
    QUEST,
    BOSS_BATTLE,
    ANALYSIS_DAILY_REPORT,
    ADMIN;

    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return Arrays.stream(values())
                .anyMatch(type -> type.name().equals(value));
    }
}
