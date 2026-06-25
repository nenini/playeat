package com.nyamnyam.coach.coin.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CoinTransaction {

    private Long transactionId;
    private Long userId;
    private String transactionType;
    private Integer amount;
    private Integer balanceAfter;
    private String sourceType;
    private Long sourceId;
    private String description;
    private LocalDateTime createdAt;
}
