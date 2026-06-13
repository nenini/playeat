package com.nyamnyam.coach.shop.dto.response;

import com.nyamnyam.coach.item.dto.response.CharacterEquipmentResponse;

import java.util.List;

public record ShopMainResponse(
        Integer balance,
        List<CharacterEquipmentResponse> equippedItems,
        List<ShopItemResponse> items
) {
}
