package com.nyamnyam.coach.nutrition.repository;

import com.nyamnyam.coach.nutrition.repository.row.PeerNutritionStatRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface PeerNutritionStatRepository {

    Optional<PeerNutritionStatRow> findStat(
            @Param("gender") String gender,
            @Param("age") int age
    );
}
