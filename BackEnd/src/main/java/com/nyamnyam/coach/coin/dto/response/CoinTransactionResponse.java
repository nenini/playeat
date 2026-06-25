package com.nyamnyam.coach.coin.dto.response;

import java.time.LocalDateTime;

public record CoinTransactionResponse(
        Long transactionId,
        String transactionType,
        Integer amount,
        Integer balanceAfter,
        String sourceType,
        Long sourceId,
        String description,
        LocalDateTime createdAt
) {
}
