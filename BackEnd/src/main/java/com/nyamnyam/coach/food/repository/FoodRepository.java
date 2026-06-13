package com.nyamnyam.coach.food.repository;

import com.nyamnyam.coach.food.entity.Food;
import com.nyamnyam.coach.food.repository.row.FrequentFoodRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FoodRepository {

    List<Food> searchByKeyword(
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    long countByKeyword(@Param("keyword") String keyword);

    Optional<Food> findById(@Param("foodId") Long foodId);

    List<FrequentFoodRow> findFrequentFoods(
            @Param("userId") Long userId,
            @Param("limit") int limit
    );
}
