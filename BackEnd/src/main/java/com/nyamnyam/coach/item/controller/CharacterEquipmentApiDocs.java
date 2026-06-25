package com.nyamnyam.coach.item.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.item.dto.request.CharacterEquipmentRequest;
import com.nyamnyam.coach.item.dto.response.CharacterEquipmentListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Character Equipment", description = "캐릭터 장착 아이템 API")
@SecurityRequirement(name = "BearerAuth")
public interface CharacterEquipmentApiDocs {

    @Operation(summary = "내 캐릭터 장착 아이템 조회")
    ResponseEntity<ApiResponse<CharacterEquipmentListResponse>> getMyEquipments(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "캐릭터 아이템 장착")
    ResponseEntity<ApiResponse<CharacterEquipmentListResponse>> equipItem(
            @Parameter(hidden = true) Authentication authentication,
            CharacterEquipmentRequest request
    );

    @Operation(summary = "캐릭터 아이템 장착 해제")
    ResponseEntity<ApiResponse<CharacterEquipmentListResponse>> unequipItem(
            @Parameter(hidden = true) Authentication authentication,
            String slotType
    );
}
