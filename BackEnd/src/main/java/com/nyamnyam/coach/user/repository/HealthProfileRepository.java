package com.nyamnyam.coach.user.repository;

import com.nyamnyam.coach.user.entity.HealthProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface HealthProfileRepository {

    void save(HealthProfile healthProfile);

    Optional<HealthProfile> findByUserId(@Param("userId") Long userId);

    int updateByUserId(HealthProfile healthProfile);

    boolean existsByUserId(@Param("userId") Long userId);
}
