package com.nyamnyam.coach.item.dto.response;

import java.time.LocalDateTime;

public record CharacterEquipmentResponse(
        String slotType,
        Boolean equipped,
        Long userItemId,
        Long itemId,
        String name,
        String description,
        String imageUrl,
        String effectValue,
        LocalDateTime equippedAt
) {
}
