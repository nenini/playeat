package com.nyamnyam.coach.item.service;

import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.ItemErrorCode;
import com.nyamnyam.coach.item.dto.response.UserItemListResponse;
import com.nyamnyam.coach.item.dto.response.UserItemResponse;
import com.nyamnyam.coach.item.entity.Item;
import com.nyamnyam.coach.item.entity.UserItem;
import com.nyamnyam.coach.item.entity.UserItemAcquiredType;
import com.nyamnyam.coach.item.repository.ItemRepository;
import com.nyamnyam.coach.item.repository.row.UserItemRow;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public UserItemListResponse getMyItems(Long userId) {
        ensureDefaultItems(userId);
        return new UserItemListResponse(itemRepository.findUserItemsByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList());
    }

    @Transactional
    public UserItemResponse getMyItemDetail(Long userId, Long userItemId) {
        ensureDefaultItems(userId);
        UserItemRow row = findOwnedUserItem(userId, userItemId);
        return toResponse(row);
    }

    @Transactional
    public void ensureDefaultItems(Long userId) {
        for (Item item : itemRepository.findDefaultItems()) {
            if (!itemRepository.existsUserItem(userId, item.getItemId())) {
                try {
                    itemRepository.insertUserItem(UserItem.builder()
                            .userId(userId)
                            .itemId(item.getItemId())
                            .acquiredType(UserItemAcquiredType.DEFAULT.name())
                            .build());
                } catch (DuplicateKeyException ignored) {
                    // Another request may have inserted the default item first.
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public UserItemRow findOwnedUserItem(Long userId, Long userItemId) {
        UserItemRow row = itemRepository.findUserItemById(userItemId)
                .orElseThrow(() -> new BusinessException(ItemErrorCode.USER_ITEM_NOT_FOUND));
        if (!userId.equals(row.getUserId())) {
            throw new BusinessException(ItemErrorCode.USER_ITEM_ACCESS_DENIED);
        }
        return row;
    }

    @Transactional(readOnly = true)
    public UserItemRow findOwnedUserItemByItemId(Long userId, Long itemId) {
        return itemRepository.findUserItemByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new BusinessException(ItemErrorCode.ITEM_NOT_OWNED));
    }

    private UserItemResponse toResponse(UserItemRow row) {
        return new UserItemResponse(
                row.getUserItemId(),
                row.getItemId(),
                row.getName(),
                row.getDescription(),
                row.getItemType(),
                row.getSlotType(),
                row.getImageUrl(),
                row.getAcquiredType(),
                row.getAcquiredAt(),
                Boolean.TRUE.equals(row.getEquipped())
        );
    }
}
