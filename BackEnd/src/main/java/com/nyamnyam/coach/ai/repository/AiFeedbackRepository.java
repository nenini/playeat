package com.nyamnyam.coach.ai.repository;

import com.nyamnyam.coach.ai.entity.AiFeedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface AiFeedbackRepository {

    void insert(AiFeedback feedback);

    Optional<AiFeedback> findLatestByUserIdAndDietId(
            @Param("userId") Long userId,
            @Param("dietId") Long dietId
    );
}
