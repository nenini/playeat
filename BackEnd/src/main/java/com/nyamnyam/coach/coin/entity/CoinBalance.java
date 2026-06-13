package com.nyamnyam.coach.coin.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CoinBalance {

    private Long userId;
    private Integer balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
