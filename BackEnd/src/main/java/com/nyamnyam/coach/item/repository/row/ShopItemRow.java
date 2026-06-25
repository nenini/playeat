package com.nyamnyam.coach.item.repository.row;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopItemRow {

    private Long itemId;
    private String name;
    private String description;
    private String itemType;
    private String slotType;
    private Integer price;
    private String imageUrl;
    private String effectValue;
    private Boolean defaultItem;
    private Boolean purchasable;
    private Boolean active;
    private Integer sortOrder;
    private Long userItemId;
    private Boolean owned;
    private Boolean equipped;
}
