package com.nyamnyam.coach.nutrition.repository;

import com.nyamnyam.coach.nutrition.repository.row.DailyNutritionAggregateRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface NutritionRepository {

    DailyNutritionAggregateRow findDailyAggregate(
            @Param("userId") Long userId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );
}
