package com.nyamnyam.coach.nutrition.repository;

import com.nyamnyam.coach.nutrition.repository.row.NutritionReferenceStandardRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface NutritionReferenceRepository {

    Optional<NutritionReferenceStandardRow> findStandard(
            @Param("gender") String gender,
            @Param("age") int age
    );
}
