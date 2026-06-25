package com.nyamnyam.coach.item.repository.row;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserItemRow {

    private Long userItemId;
    private Long userId;
    private Long itemId;
    private String name;
    private String description;
    private String itemType;
    private String slotType;
    private String imageUrl;
    private String effectValue;
    private String acquiredType;
    private LocalDateTime acquiredAt;
    private Boolean equipped;
    private Boolean active;
}
