package com.nyamnyam.coach.item.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Item {

    private Long itemId;
    private String name;
    private String description;
    private String itemType;
    private String slotType;
    private Integer price;
    private String imageUrl;
    private Boolean defaultItem;
    private Boolean purchasable;
    private Boolean active;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
