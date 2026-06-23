package com.nyamnyam.coach.item.entity;

import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.ItemErrorCode;

public enum ItemSlotType {
    HAND,
    HEAD,
    CHARACTER,
    BACKGROUND;

    public static ItemSlotType from(String slotType) {
        if (slotType == null) {
            throw new BusinessException(ItemErrorCode.INVALID_SLOT_TYPE);
        }
        try {
            return ItemSlotType.valueOf(slotType.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ItemErrorCode.INVALID_SLOT_TYPE);
        }
    }
}
