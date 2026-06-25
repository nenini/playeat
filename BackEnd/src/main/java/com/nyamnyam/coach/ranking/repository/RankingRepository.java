package com.nyamnyam.coach.ranking.repository;

import com.nyamnyam.coach.ranking.repository.row.BossInfoRow;
import com.nyamnyam.coach.ranking.repository.row.BossRankingRow;
import com.nyamnyam.coach.ranking.repository.row.GuildWeeklyStatRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface RankingRepository {

    Optional<Long> findMyGuildId(@Param("userId") Long userId);

    List<GuildWeeklyStatRow> findGuildWeeklyStats(
            @Param("weekStartDate") LocalDate weekStartDate,
            @Param("weekEndDate") LocalDate weekEndDate
    );

    Optional<BossInfoRow> findBossInfo(@Param("bossId") Long bossId);

    List<BossRankingRow> findBossRankingByBossId(@Param("bossId") Long bossId);
}
