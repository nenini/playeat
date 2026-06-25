package com.nyamnyam.coach.shop.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.shop.dto.response.ItemPurchaseResponse;
import com.nyamnyam.coach.shop.dto.response.ShopItemDetailResponse;
import com.nyamnyam.coach.shop.dto.response.ShopItemListResponse;
import com.nyamnyam.coach.shop.dto.response.ShopMainResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Shop", description = "상점 API")
@SecurityRequirement(name = "BearerAuth")
public interface ShopApiDocs {

    @Operation(summary = "상점 메인 조회")
    ResponseEntity<ApiResponse<ShopMainResponse>> getShopMain(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "상점 아이템 목록 조회")
    ResponseEntity<ApiResponse<ShopItemListResponse>> getShopItems(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "상점 아이템 상세 조회")
    ResponseEntity<ApiResponse<ShopItemDetailResponse>> getShopItemDetail(
            @Parameter(hidden = true) Authentication authentication,
            Long itemId
    );

    @Operation(summary = "상점 아이템 구매")
    ResponseEntity<ApiResponse<ItemPurchaseResponse>> purchaseItem(
            @Parameter(hidden = true) Authentication authentication,
            Long itemId
    );
}
