package com.nyamnyam.coach.shop.dto.response;

public record ShopItemResponse(
        Long itemId,
        String name,
        String description,
        String itemType,
        String slotType,
        Integer price,
        String imageUrl,
        String effectValue,
        Boolean defaultItem,
        Boolean purchasable,
        Boolean owned,
        Boolean equipped,
        Long userItemId
) {
}
