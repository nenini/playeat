package com.nyamnyam.coach.item.dto.request;

import jakarta.validation.constraints.NotNull;

public record CharacterEquipmentRequest(
        @NotNull(message = "장착할 보유 아이템 ID는 필수입니다.")
        Long userItemId
) {
}
