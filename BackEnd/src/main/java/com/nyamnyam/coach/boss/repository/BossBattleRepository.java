package com.nyamnyam.coach.boss.repository;

import com.nyamnyam.coach.boss.entity.BossBattle;
import com.nyamnyam.coach.boss.entity.BossBattleCondition;
import com.nyamnyam.coach.boss.repository.row.BossBattleConditionRow;
import com.nyamnyam.coach.boss.repository.row.BossBattleDamageLogRow;
import com.nyamnyam.coach.boss.repository.row.BossBattleRow;
import com.nyamnyam.coach.boss.repository.row.BossCommonConditionRow;
import com.nyamnyam.coach.boss.repository.row.BossRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BossBattleRepository {

    Optional<BossRow> findActiveBossById(@Param("bossId") Long bossId);

    Optional<Long> findCurrentSeasonId();

    boolean existsInProgressBattleByGuildId(@Param("guildId") Long guildId);

    boolean existsBattleByGuildIdAndSeasonId(
            @Param("guildId") Long guildId,
            @Param("seasonId") Long seasonId
    );

    void insertBossBattle(BossBattle battle);

    List<BossCommonConditionRow> findBossCommonConditionsBySeasonId(@Param("seasonId") Long seasonId);

    void insertBossBattleCondition(BossBattleCondition condition);

    Optional<BossBattleRow> findCurrentBattleByGuildId(@Param("guildId") Long guildId);

    Optional<BossBattleRow> findBattleDetailById(@Param("battleId") Long battleId);

    List<BossBattleConditionRow> findBattleConditionsByBattleId(@Param("battleId") Long battleId);

    List<BossBattleDamageLogRow> findRecentDamageLogsByBattleId(
            @Param("battleId") Long battleId,
            @Param("limit") int limit
    );

    Optional<BossBattleRow> findBattleHpById(@Param("battleId") Long battleId);

    List<BossBattleRow> findBattleHistoryByGuildId(
            @Param("guildId") Long guildId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    int countBattleHistoryByGuildId(@Param("guildId") Long guildId);

    Optional<Long> findGuildIdByBattleId(@Param("battleId") Long battleId);
}
