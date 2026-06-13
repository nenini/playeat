package com.nyamnyam.coach.coin.dto.response;

public record CoinBalanceResponse(
        Long userId,
        Integer balance
) {
}
