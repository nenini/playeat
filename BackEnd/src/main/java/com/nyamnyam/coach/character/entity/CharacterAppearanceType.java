package com.nyamnyam.coach.character.entity;

import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.CharacterErrorCode;

public enum CharacterAppearanceType {
    NYAMNYAM,
    PENGUIN,
    DOG,
    NORMAL,
    DEFAULT;

    public static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return NYAMNYAM.name();
        }
        String key = value.trim().toUpperCase();
        if (NORMAL.name().equals(key) || DEFAULT.name().equals(key)) {
            return NYAMNYAM.name();
        }
        try {
            CharacterAppearanceType type = CharacterAppearanceType.valueOf(key);
            if (type == NORMAL || type == DEFAULT) {
                return NYAMNYAM.name();
            }
            return type.name();
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(CharacterErrorCode.INVALID_CHARACTER_STATE);
        }
    }
}
