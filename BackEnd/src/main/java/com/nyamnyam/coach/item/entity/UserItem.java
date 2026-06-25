package com.nyamnyam.coach.item.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserItem {

    private Long userItemId;
    private Long userId;
    private Long itemId;
    private String acquiredType;
    private Long acquiredSourceId;
    private LocalDateTime acquiredAt;
}
