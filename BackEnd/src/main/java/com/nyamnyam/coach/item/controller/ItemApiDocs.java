package com.nyamnyam.coach.item.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.item.dto.response.UserItemListResponse;
import com.nyamnyam.coach.item.dto.response.UserItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Item", description = "보유 아이템 API")
@SecurityRequirement(name = "BearerAuth")
public interface ItemApiDocs {

    @Operation(summary = "내 보유 아이템 목록 조회")
    ResponseEntity<ApiResponse<UserItemListResponse>> getMyItems(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "내 보유 아이템 상세 조회")
    ResponseEntity<ApiResponse<UserItemResponse>> getMyItemDetail(
            @Parameter(hidden = true) Authentication authentication,
            Long userItemId
    );
}
