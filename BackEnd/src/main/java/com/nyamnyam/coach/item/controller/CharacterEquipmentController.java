package com.nyamnyam.coach.item.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.item.dto.request.CharacterEquipmentRequest;
import com.nyamnyam.coach.item.dto.response.CharacterEquipmentListResponse;
import com.nyamnyam.coach.item.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/characters/me/equipments")
public class CharacterEquipmentController implements CharacterEquipmentApiDocs {

    private final EquipmentService equipmentService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<CharacterEquipmentListResponse>> getMyEquipments(Authentication authentication) {
        CharacterEquipmentListResponse response = equipmentService.getMyEquipments(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "내 캐릭터 장착 아이템 조회에 성공했습니다."));
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<CharacterEquipmentListResponse>> equipItem(
            Authentication authentication,
            @Valid @RequestBody CharacterEquipmentRequest request
    ) {
        CharacterEquipmentListResponse response = equipmentService.equipItem(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "캐릭터 아이템 장착에 성공했습니다."));
    }

    @Override
    @DeleteMapping("/{slotType}")
    public ResponseEntity<ApiResponse<CharacterEquipmentListResponse>> unequipItem(
            Authentication authentication,
            @PathVariable String slotType
    ) {
        CharacterEquipmentListResponse response = equipmentService.unequipItem(authenticatedUserId(authentication), slotType);
        return ResponseEntity.ok(ApiResponse.success(response, "캐릭터 아이템 장착 해제에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
