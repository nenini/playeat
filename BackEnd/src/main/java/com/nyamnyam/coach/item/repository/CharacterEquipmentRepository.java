package com.nyamnyam.coach.item.repository;

import com.nyamnyam.coach.item.repository.row.CharacterEquipmentRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CharacterEquipmentRepository {

    List<CharacterEquipmentRow> findByCharacterId(@Param("characterId") Long characterId);

    Optional<CharacterEquipmentRow> findByCharacterIdAndSlotType(
            @Param("characterId") Long characterId,
            @Param("slotType") String slotType
    );

    void upsertEquipment(
            @Param("characterId") Long characterId,
            @Param("slotType") String slotType,
            @Param("userItemId") Long userItemId
    );

    int deleteByCharacterIdAndSlotType(
            @Param("characterId") Long characterId,
            @Param("slotType") String slotType
    );
}
