package com.nyamnyam.coach.item.service;

import com.nyamnyam.coach.character.entity.CharacterAppearanceType;
import com.nyamnyam.coach.character.entity.CharacterEntity;
import com.nyamnyam.coach.character.repository.CharacterRepository;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.CharacterErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.ItemErrorCode;
import com.nyamnyam.coach.item.dto.request.CharacterEquipmentRequest;
import com.nyamnyam.coach.item.dto.response.CharacterEquipmentListResponse;
import com.nyamnyam.coach.item.dto.response.CharacterEquipmentResponse;
import com.nyamnyam.coach.item.entity.ItemSlotType;
import com.nyamnyam.coach.item.entity.ItemType;
import com.nyamnyam.coach.item.repository.CharacterEquipmentRepository;
import com.nyamnyam.coach.item.repository.ItemRepository;
import com.nyamnyam.coach.item.repository.row.CharacterEquipmentRow;
import com.nyamnyam.coach.item.repository.row.UserItemRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final CharacterRepository characterRepository;
    private final CharacterEquipmentRepository characterEquipmentRepository;
    private final ItemService itemService;

    @Transactional
    public CharacterEquipmentListResponse getMyEquipments(Long userId) {
        CharacterEntity character = findCharacter(userId);
        itemService.ensureDefaultItems(userId);

        return toListResponse(
                character.getCharacterId(),
                characterEquipmentRepository.findByCharacterId(character.getCharacterId())
        );
    }

    @Transactional
    public CharacterEquipmentListResponse equipItem(Long userId, CharacterEquipmentRequest request) {
        CharacterEntity character = findCharacter(userId);
        itemService.ensureDefaultItems(userId);
        UserItemRow userItem = itemService.findOwnedUserItem(userId, request.userItemId());
        validateEquippable(userItem);

        characterEquipmentRepository.upsertEquipment(
                character.getCharacterId(),
                userItem.getSlotType(),
                userItem.getUserItemId()
        );
        if (ItemSlotType.CHARACTER.name().equals(userItem.getSlotType())) {
            characterRepository.updateAppearanceType(userId, CharacterAppearanceType.normalize(userItem.getEffectValue()));
        }
        return toListResponse(character.getCharacterId(), characterEquipmentRepository.findByCharacterId(character.getCharacterId()));
    }

    @Transactional
    public CharacterEquipmentListResponse unequipItem(Long userId, String slotType) {
        CharacterEntity character = findCharacter(userId);
        ItemSlotType slot = ItemSlotType.from(slotType);
        itemService.ensureDefaultItems(userId);

        characterEquipmentRepository.deleteByCharacterIdAndSlotType(
                character.getCharacterId(),
                slot.name()
        );
        if (slot == ItemSlotType.CHARACTER) {
            characterRepository.updateAppearanceType(userId, CharacterAppearanceType.NYAMNYAM.name());
        }

        return toListResponse(
                character.getCharacterId(),
                characterEquipmentRepository.findByCharacterId(character.getCharacterId())
        );
    }


    private CharacterEntity findCharacter(Long userId) {
        return characterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(CharacterErrorCode.CHARACTER_NOT_FOUND));
    }

    private void validateEquippable(UserItemRow userItem) {
        if (!Boolean.TRUE.equals(userItem.getActive())) {
            throw new BusinessException(ItemErrorCode.ITEM_NOT_EQUIPPABLE);
        }
        ItemSlotType slotType = ItemSlotType.from(userItem.getSlotType());
        if (!ItemType.EQUIPMENT.name().equals(userItem.getItemType())
                && !ItemType.CHARACTER.name().equals(userItem.getItemType())
                && !ItemType.BACKGROUND.name().equals(userItem.getItemType())) {
            throw new BusinessException(ItemErrorCode.ITEM_NOT_EQUIPPABLE);
        }
        if (ItemType.CHARACTER.name().equals(userItem.getItemType()) && slotType != ItemSlotType.CHARACTER) {
            throw new BusinessException(ItemErrorCode.ITEM_NOT_EQUIPPABLE);
        }
        if (ItemType.BACKGROUND.name().equals(userItem.getItemType()) && slotType != ItemSlotType.BACKGROUND) {
            throw new BusinessException(ItemErrorCode.ITEM_NOT_EQUIPPABLE);
        }
    }

    private CharacterEquipmentListResponse toListResponse(Long characterId, List<CharacterEquipmentRow> rows) {
        List<CharacterEquipmentResponse> responses = new ArrayList<>();
        for (ItemSlotType slotType : ItemSlotType.values()) {
            rows.stream()
                    .filter(row -> slotType.name().equals(row.getSlotType()))
                    .findFirst()
                    .map(this::toResponse)
                    .ifPresentOrElse(
                            responses::add,
                            () -> responses.add(emptyResponse(slotType))
                    );
        }
        responses.sort(Comparator.comparingInt(response -> slotOrder(response.slotType())));
        return new CharacterEquipmentListResponse(characterId, responses);
    }

    private CharacterEquipmentResponse toResponse(CharacterEquipmentRow row) {
        return new CharacterEquipmentResponse(
                row.getSlotType(),
                true,
                row.getUserItemId(),
                row.getItemId(),
                row.getName(),
                row.getDescription(),
                row.getImageUrl(),
                row.getEffectValue(),
                row.getEquippedAt()
        );
    }

    private CharacterEquipmentResponse emptyResponse(ItemSlotType slotType) {
        return new CharacterEquipmentResponse(
                slotType.name(),
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private int slotOrder(String slotType) {
        if (ItemSlotType.CHARACTER.name().equals(slotType)) return 0;
        if (ItemSlotType.HEAD.name().equals(slotType)) return 1;
        if (ItemSlotType.HAND.name().equals(slotType)) return 2;
        return ItemSlotType.BACKGROUND.name().equals(slotType) ? 3 : 9;
    }
}
