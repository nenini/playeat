package com.nyamnyam.coach.coin.controller;

import com.nyamnyam.coach.coin.dto.response.CoinBalanceResponse;
import com.nyamnyam.coach.coin.dto.response.CoinTransactionListResponse;
import com.nyamnyam.coach.coin.service.CoinService;
import com.nyamnyam.coach.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/coins")
public class CoinController implements CoinApiDocs {

    private final CoinService coinService;

    @Override
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CoinBalanceResponse>> getMyBalance(Authentication authentication) {
        CoinBalanceResponse response = coinService.getMyBalance(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "냠냠코인 잔액 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/me/transactions")
    public ResponseEntity<ApiResponse<CoinTransactionListResponse>> getMyTransactions(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        CoinTransactionListResponse response = coinService.getMyTransactions(
                authenticatedUserId(authentication),
                page,
                size
        );
        return ResponseEntity.ok(ApiResponse.success(response, "냠냠코인 거래 내역 조회에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
