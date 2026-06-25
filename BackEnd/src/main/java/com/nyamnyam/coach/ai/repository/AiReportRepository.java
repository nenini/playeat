package com.nyamnyam.coach.ai.repository;

import com.nyamnyam.coach.ai.entity.AiReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Optional;

@Mapper
public interface AiReportRepository {

    void insert(AiReport report);

    Optional<AiReport> findByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("reportType") String reportType,
            @Param("periodStart") LocalDate periodStart,
            @Param("periodEnd") LocalDate periodEnd
    );
}
