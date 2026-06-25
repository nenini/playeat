package com.nyamnyam.coach.ranking.repository;

import com.nyamnyam.coach.dashboard.repository.row.DailyScoreRow;
import com.nyamnyam.coach.ranking.entity.GuildScoreLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface GuildScoreRepository {

    void insertScoreLog(GuildScoreLog log);

    int sumScoreByGuildAndDate(
            @Param("guildId") Long guildId,
            @Param("scoreDate") LocalDate scoreDate
    );

    int sumScoreByGuildAndWeek(
            @Param("guildId") Long guildId,
            @Param("weekStartDate") LocalDate weekStartDate,
            @Param("weekEndDate") LocalDate weekEndDate
    );

    List<DailyScoreRow> findDailyScores(
            @Param("guildId") Long guildId,
            @Param("weekStartDate") LocalDate weekStartDate,
            @Param("weekEndDate") LocalDate weekEndDate
    );

    boolean existsScoreLog(
            @Param("sourceType") String sourceType,
            @Param("sourceId") Long sourceId
    );
}
