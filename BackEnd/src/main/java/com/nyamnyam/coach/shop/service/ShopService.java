package com.nyamnyam.coach.shop.service;

import com.nyamnyam.coach.coin.dto.response.CoinBalanceResponse;
import com.nyamnyam.coach.coin.entity.CoinSourceType;
import com.nyamnyam.coach.coin.service.CoinService;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.ItemErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.ShopErrorCode;
import com.nyamnyam.coach.item.dto.response.CharacterEquipmentListResponse;
import com.nyamnyam.coach.item.entity.UserItem;
import com.nyamnyam.coach.item.entity.UserItemAcquiredType;
import com.nyamnyam.coach.item.repository.ItemRepository;
import com.nyamnyam.coach.item.repository.row.ShopItemRow;
import com.nyamnyam.coach.item.service.EquipmentService;
import com.nyamnyam.coach.item.service.ItemService;
import com.nyamnyam.coach.shop.dto.response.ItemPurchaseResponse;
import com.nyamnyam.coach.shop.dto.response.ShopItemDetailResponse;
import com.nyamnyam.coach.shop.dto.response.ShopItemListResponse;
import com.nyamnyam.coach.shop.dto.response.ShopItemResponse;
import com.nyamnyam.coach.shop.dto.response.ShopMainResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final EquipmentService equipmentService;
    private final CoinService coinService;

    @Transactional
    public ShopMainResponse getShopMain(Long userId) {
        coinService.ensureBalance(userId);
        itemService.ensureDefaultItems(userId);
        CharacterEquipmentListResponse equipments = equipmentService.getMyEquipments(userId);
        return new ShopMainResponse(
                coinService.getMyBalance(userId).balance(),
                equipments.equipments(),
                getShopItems(userId).items()
        );
    }

    @Transactional
    public ShopItemListResponse getShopItems(Long userId) {
        itemService.ensureDefaultItems(userId);
        return new ShopItemListResponse(itemRepository.findActiveItems(userId)
                .stream()
                .map(this::toResponse)
                .toList());
    }

    @Transactional
    public ShopItemDetailResponse getShopItemDetail(Long userId, Long itemId) {
        itemService.ensureDefaultItems(userId);
        ShopItemRow row = itemRepository.findActiveItemById(itemId, userId)
                .orElseThrow(() -> new BusinessException(ShopErrorCode.SHOP_ITEM_NOT_FOUND));
        return new ShopItemDetailResponse(
                row.getItemId(),
                row.getName(),
                row.getDescription(),
                row.getItemType(),
                row.getSlotType(),
                row.getPrice(),
                row.getImageUrl(),
                Boolean.TRUE.equals(row.getDefaultItem()),
                Boolean.TRUE.equals(row.getPurchasable()),
                Boolean.TRUE.equals(row.getOwned()),
                Boolean.TRUE.equals(row.getEquipped()),
                row.getUserItemId()
        );
    }

    @Transactional
    public ItemPurchaseResponse purchaseItem(Long userId, Long itemId) {
        coinService.ensureBalance(userId);
        itemService.ensureDefaultItems(userId);
        ShopItemRow item = itemRepository.findActiveItemById(itemId, userId)
                .orElseThrow(() -> new BusinessException(ShopErrorCode.SHOP_ITEM_NOT_FOUND));
        validatePurchasable(item);
        if (Boolean.TRUE.equals(item.getOwned())) {
            throw new BusinessException(ItemErrorCode.ITEM_ALREADY_OWNED);
        }

        CoinBalanceResponse balance = coinService.spend(
                userId,
                item.getPrice(),
                CoinSourceType.SHOP_PURCHASE,
                itemId,
                "아이템 구매: " + item.getName()
        );

        UserItem userItem = UserItem.builder()
                .userId(userId)
                .itemId(itemId)
                .acquiredType(UserItemAcquiredType.PURCHASE.name())
                .acquiredSourceId(itemId)
                .build();
        itemRepository.insertUserItem(userItem);

        return new ItemPurchaseResponse(
                itemId,
                userItem.getUserItemId(),
                item.getName(),
                item.getPrice(),
                balance.balance(),
                LocalDateTime.now()
        );
    }

    private void validatePurchasable(ShopItemRow item) {
        if (!Boolean.TRUE.equals(item.getActive())) {
            throw new BusinessException(ShopErrorCode.SHOP_ITEM_INACTIVE);
        }
        if (!Boolean.TRUE.equals(item.getPurchasable())) {
            throw new BusinessException(ShopErrorCode.SHOP_ITEM_NOT_PURCHASABLE);
        }
    }

    private ShopItemResponse toResponse(ShopItemRow row) {
        return new ShopItemResponse(
                row.getItemId(),
                row.getName(),
                row.getDescription(),
                row.getItemType(),
                row.getSlotType(),
                row.getPrice(),
                row.getImageUrl(),
                Boolean.TRUE.equals(row.getDefaultItem()),
                Boolean.TRUE.equals(row.getPurchasable()),
                Boolean.TRUE.equals(row.getOwned()),
                Boolean.TRUE.equals(row.getEquipped()),
                row.getUserItemId()
        );
    }
}
