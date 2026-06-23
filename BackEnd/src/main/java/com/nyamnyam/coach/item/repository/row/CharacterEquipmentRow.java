package com.nyamnyam.coach.item.repository.row;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CharacterEquipmentRow {

    private Long equipmentId;
    private Long characterId;
    private String slotType;
    private Long userItemId;
    private Long itemId;
    private String name;
    private String description;
    private String imageUrl;
    private String effectValue;
    private LocalDateTime equippedAt;
}
