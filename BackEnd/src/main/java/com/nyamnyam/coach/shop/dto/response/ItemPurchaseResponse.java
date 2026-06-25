package com.nyamnyam.coach.shop.dto.response;

import java.time.LocalDateTime;

public record ItemPurchaseResponse(
        Long itemId,
        Long userItemId,
        String name,
        Integer price,
        Integer balanceAfter,
        LocalDateTime purchasedAt
) {
}
