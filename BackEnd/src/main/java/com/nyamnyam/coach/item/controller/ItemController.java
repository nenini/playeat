package com.nyamnyam.coach.item.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.item.dto.response.UserItemListResponse;
import com.nyamnyam.coach.item.dto.response.UserItemResponse;
import com.nyamnyam.coach.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/items")
public class ItemController implements ItemApiDocs {

    private final ItemService itemService;

    @Override
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserItemListResponse>> getMyItems(Authentication authentication) {
        UserItemListResponse response = itemService.getMyItems(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "내 보유 아이템 목록 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/me/{userItemId}")
    public ResponseEntity<ApiResponse<UserItemResponse>> getMyItemDetail(
            Authentication authentication,
            @PathVariable Long userItemId
    ) {
        UserItemResponse response = itemService.getMyItemDetail(authenticatedUserId(authentication), userItemId);
        return ResponseEntity.ok(ApiResponse.success(response, "내 보유 아이템 상세 조회에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
