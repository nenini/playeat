package com.nyamnyam.coach.item.repository;

import com.nyamnyam.coach.item.entity.Item;
import com.nyamnyam.coach.item.entity.UserItem;
import com.nyamnyam.coach.item.repository.row.ShopItemRow;
import com.nyamnyam.coach.item.repository.row.UserItemRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ItemRepository {

    List<ShopItemRow> findActiveItems(@Param("userId") Long userId);

    Optional<ShopItemRow> findActiveItemById(
            @Param("itemId") Long itemId,
            @Param("userId") Long userId
    );

    Optional<Item> findItemById(@Param("itemId") Long itemId);

    List<Item> findDefaultItems();

    List<UserItemRow> findUserItemsByUserId(@Param("userId") Long userId);

    Optional<UserItemRow> findUserItemById(@Param("userItemId") Long userItemId);

    boolean existsUserItem(
            @Param("userId") Long userId,
            @Param("itemId") Long itemId
    );

    void insertUserItem(UserItem userItem);

    Optional<UserItemRow> findUserItemByUserIdAndItemId(
            @Param("userId") Long userId,
            @Param("itemId") Long itemId
    );
}
