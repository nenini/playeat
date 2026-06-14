package com.nyamnyam.coach.coach.repository;

import com.nyamnyam.coach.coach.entity.Coach;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CoachRepository {

    List<Coach> findActiveCoaches();

    Optional<Coach> findById(@Param("coachId") Long coachId);

    Optional<Coach> findSelectedByUserId(@Param("userId") Long userId);

    Optional<Coach> findDefaultCoach();

    void updateSelectedCoach(
            @Param("userId") Long userId,
            @Param("coachId") Long coachId
    );
}
