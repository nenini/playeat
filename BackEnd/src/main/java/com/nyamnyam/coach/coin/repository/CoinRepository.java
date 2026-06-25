package com.nyamnyam.coach.coin.repository;

import com.nyamnyam.coach.coin.entity.CoinBalance;
import com.nyamnyam.coach.coin.entity.CoinTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CoinRepository {

    Optional<CoinBalance> findBalanceByUserId(@Param("userId") Long userId);

    int insertInitialBalance(@Param("userId") Long userId);

    int increaseBalance(
            @Param("userId") Long userId,
            @Param("amount") int amount
    );

    int decreaseBalance(
            @Param("userId") Long userId,
            @Param("amount") int amount
    );

    void insertTransaction(CoinTransaction transaction);

    List<CoinTransaction> findTransactionsByUserId(
            @Param("userId") Long userId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    int countTransactionsByUserId(@Param("userId") Long userId);
}
