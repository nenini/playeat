package com.nyamnyam.coach.coin.service;

import com.nyamnyam.coach.coin.dto.response.CoinBalanceResponse;
import com.nyamnyam.coach.coin.dto.response.CoinTransactionListResponse;
import com.nyamnyam.coach.coin.dto.response.CoinTransactionResponse;
import com.nyamnyam.coach.coin.entity.CoinSourceType;
import com.nyamnyam.coach.coin.entity.CoinTransaction;
import com.nyamnyam.coach.coin.entity.CoinTransactionType;
import com.nyamnyam.coach.coin.repository.CoinRepository;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.CoinErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoinService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final CoinRepository coinRepository;

    @Transactional
    public CoinBalanceResponse getMyBalance(Long userId) {
        ensureBalance(userId);
        return new CoinBalanceResponse(userId, currentBalance(userId));
    }

    @Transactional
    public CoinTransactionListResponse getMyTransactions(Long userId, Integer page, Integer size) {
        ensureBalance(userId);
        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        int offset = normalizedPage * normalizedSize;

        List<CoinTransactionResponse> transactions = coinRepository.findTransactionsByUserId(
                        userId,
                        normalizedSize,
                        offset
                )
                .stream()
                .map(this::toResponse)
                .toList();
        int totalCount = coinRepository.countTransactionsByUserId(userId);
        return new CoinTransactionListResponse(
                transactions,
                normalizedPage,
                normalizedSize,
                offset + normalizedSize < totalCount
        );
    }

    @Transactional
    public CoinBalanceResponse earn(
            Long userId,
            int amount,
            CoinSourceType sourceType,
            Long sourceId,
            String description
    ) {
        validatePositiveAmount(amount);
        ensureBalance(userId);
        coinRepository.increaseBalance(userId, amount);
        int balanceAfter = currentBalance(userId);
        insertTransaction(userId, CoinTransactionType.EARN, amount, balanceAfter, sourceType, sourceId, description);
        return new CoinBalanceResponse(userId, balanceAfter);
    }

    @Transactional
    public CoinBalanceResponse spend(
            Long userId,
            int amount,
            CoinSourceType sourceType,
            Long sourceId,
            String description
    ) {
        validatePositiveAmount(amount);
        ensureBalance(userId);
        int updated = coinRepository.decreaseBalance(userId, amount);
        if (updated == 0) {
            throw new BusinessException(CoinErrorCode.INSUFFICIENT_COIN);
        }
        int balanceAfter = currentBalance(userId);
        insertTransaction(userId, CoinTransactionType.SPEND, -amount, balanceAfter, sourceType, sourceId, description);
        return new CoinBalanceResponse(userId, balanceAfter);
    }

    @Transactional
    public void ensureBalance(Long userId) {
        coinRepository.insertInitialBalance(userId);
    }

    private int currentBalance(Long userId) {
        return coinRepository.findBalanceByUserId(userId)
                .orElseThrow(() -> new BusinessException(CoinErrorCode.COIN_BALANCE_NOT_FOUND))
                .getBalance();
    }

    private void insertTransaction(
            Long userId,
            CoinTransactionType transactionType,
            int amount,
            int balanceAfter,
            CoinSourceType sourceType,
            Long sourceId,
            String description
    ) {
        coinRepository.insertTransaction(CoinTransaction.builder()
                .userId(userId)
                .transactionType(transactionType.name())
                .amount(amount)
                .balanceAfter(balanceAfter)
                .sourceType(sourceType.name())
                .sourceId(sourceId)
                .description(description)
                .build());
    }

    private CoinTransactionResponse toResponse(CoinTransaction transaction) {
        return new CoinTransactionResponse(
                transaction.getTransactionId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getSourceType(),
                transaction.getSourceId(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }

    private void validatePositiveAmount(int amount) {
        if (amount <= 0) {
            throw new BusinessException(CoinErrorCode.INVALID_COIN_AMOUNT);
        }
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 0) {
            return DEFAULT_PAGE;
        }
        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }
}
