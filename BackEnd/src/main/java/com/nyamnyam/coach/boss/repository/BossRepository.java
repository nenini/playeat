package com.nyamnyam.coach.boss.repository;

import com.nyamnyam.coach.boss.repository.row.BossCommonConditionRow;
import com.nyamnyam.coach.boss.repository.row.BossRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BossRepository {

    List<BossRow> findCurrentBosses();

    Optional<BossRow> findBossById(@Param("bossId") Long bossId);

    List<BossCommonConditionRow> findCommonConditionsBySeasonId(@Param("seasonId") Long seasonId);

    Optional<Long> findCurrentSeasonId();

    boolean existsActiveBossById(@Param("bossId") Long bossId);
}
