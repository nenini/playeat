package com.nyamnyam.coach.item.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CharacterEquipment {

    private Long equipmentId;
    private Long characterId;
    private String slotType;
    private Long userItemId;
    private LocalDateTime equippedAt;
    private LocalDateTime updatedAt;
}
