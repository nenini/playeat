package com.nyamnyam.coach.coin.dto.response;

import java.util.List;

public record CoinTransactionListResponse(
        List<CoinTransactionResponse> transactions,
        Integer page,
        Integer size,
        Boolean hasNext
) {
}
