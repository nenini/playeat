package com.nyamnyam.coach.diet.repository;

import com.nyamnyam.coach.diet.entity.Diet;
import com.nyamnyam.coach.diet.entity.DietItem;
import com.nyamnyam.coach.diet.entity.MealType;
import com.nyamnyam.coach.diet.repository.row.DietItemRow;
import com.nyamnyam.coach.diet.repository.row.NutritionTargetRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface DietRepository {

    List<Diet> findByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    Optional<Diet> findByIdAndUserId(
            @Param("dietId") Long dietId,
            @Param("userId") Long userId
    );

    Optional<Diet> findByUserIdAndDateAndMealType(
            @Param("userId") Long userId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            @Param("mealType") MealType mealType
    );

    List<DietItemRow> findItemsByDietId(@Param("dietId") Long dietId);

    Optional<NutritionTargetRow> findNutritionTargetByUserId(@Param("userId") Long userId);

    void insertDiet(Diet diet);

    void insertDietItem(DietItem dietItem);

    void updateDiet(Diet diet);

    void updateDietTotals(Diet diet);

    void deleteItemsByDietId(@Param("dietId") Long dietId);

    int deleteByIdAndUserId(
            @Param("dietId") Long dietId,
            @Param("userId") Long userId
    );
}
