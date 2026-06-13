package com.nyamnyam.coach.shop.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.shop.dto.response.ItemPurchaseResponse;
import com.nyamnyam.coach.shop.dto.response.ShopItemDetailResponse;
import com.nyamnyam.coach.shop.dto.response.ShopItemListResponse;
import com.nyamnyam.coach.shop.dto.response.ShopMainResponse;
import com.nyamnyam.coach.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/shop")
public class ShopController implements ShopApiDocs {

    private final ShopService shopService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<ShopMainResponse>> getShopMain(Authentication authentication) {
        ShopMainResponse response = shopService.getShopMain(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "상점 메인 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<ShopItemListResponse>> getShopItems(Authentication authentication) {
        ShopItemListResponse response = shopService.getShopItems(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "상점 아이템 목록 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<ShopItemDetailResponse>> getShopItemDetail(
            Authentication authentication,
            @PathVariable Long itemId
    ) {
        ShopItemDetailResponse response = shopService.getShopItemDetail(authenticatedUserId(authentication), itemId);
        return ResponseEntity.ok(ApiResponse.success(response, "상점 아이템 상세 조회에 성공했습니다."));
    }

    @Override
    @PostMapping("/items/{itemId}/purchase")
    public ResponseEntity<ApiResponse<ItemPurchaseResponse>> purchaseItem(
            Authentication authentication,
            @PathVariable Long itemId
    ) {
        ItemPurchaseResponse response = shopService.purchaseItem(authenticatedUserId(authentication), itemId);
        return ResponseEntity.ok(ApiResponse.success(response, "상점 아이템 구매에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
