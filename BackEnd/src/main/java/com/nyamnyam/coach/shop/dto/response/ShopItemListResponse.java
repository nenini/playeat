package com.nyamnyam.coach.shop.dto.response;

import java.util.List;

public record ShopItemListResponse(
        List<ShopItemResponse> items
) {
}
