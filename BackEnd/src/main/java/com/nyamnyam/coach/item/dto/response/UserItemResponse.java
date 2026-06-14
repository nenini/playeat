package com.nyamnyam.coach.item.dto.response;

import java.time.LocalDateTime;

public record UserItemResponse(
        Long userItemId,
        Long itemId,
        String name,
        String description,
        String itemType,
        String slotType,
        String imageUrl,
        String acquiredType,
        LocalDateTime acquiredAt,
        Boolean equipped
) {
}
