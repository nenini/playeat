package com.nyamnyam.coach.coin.controller;

import com.nyamnyam.coach.coin.dto.response.CoinBalanceResponse;
import com.nyamnyam.coach.coin.dto.response.CoinTransactionListResponse;
import com.nyamnyam.coach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Coin", description = "냠냠코인 API")
@SecurityRequirement(name = "BearerAuth")
public interface CoinApiDocs {

    @Operation(summary = "내 냠냠코인 잔액 조회")
    ResponseEntity<ApiResponse<CoinBalanceResponse>> getMyBalance(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "내 냠냠코인 거래 내역 조회")
    ResponseEntity<ApiResponse<CoinTransactionListResponse>> getMyTransactions(
            @Parameter(hidden = true) Authentication authentication,
            Integer page,
            Integer size
    );
}
