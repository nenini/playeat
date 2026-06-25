package com.nyamnyam.coach.item.dto.response;

import java.util.List;

public record CharacterEquipmentListResponse(
        Long characterId,
        List<CharacterEquipmentResponse> equipments
) {
}
