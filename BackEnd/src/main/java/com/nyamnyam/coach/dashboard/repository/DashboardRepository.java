package com.nyamnyam.coach.dashboard.repository;

import com.nyamnyam.coach.dashboard.repository.row.BattleDashboardRow;
import com.nyamnyam.coach.dashboard.repository.row.DailyCountRow;
import com.nyamnyam.coach.dashboard.repository.row.GuildBasicInfoRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface DashboardRepository {

    Optional<GuildBasicInfoRow> findGuildBasicInfo(@Param("guildId") Long guildId);

    int countActiveGuildMembers(@Param("guildId") Long guildId);

    int countRecordedMemberDates(
            @Param("guildId") Long guildId,
            @Param("weekStartDate") LocalDate weekStartDate,
            @Param("weekEndDate") LocalDate weekEndDate
    );

    List<DailyCountRow> findDailyRecordedMembers(
            @Param("guildId") Long guildId,
            @Param("weekStartDate") LocalDate weekStartDate,
            @Param("weekEndDate") LocalDate weekEndDate
    );

    Optional<BattleDashboardRow> findCurrentOrLatestBattleByGuildId(@Param("guildId") Long guildId);

    Optional<BattleDashboardRow> findBattleDashboardById(@Param("battleId") Long battleId);

    int countQuestsByGuildAndWeek(
            @Param("guildId") Long guildId,
            @Param("weekStartDate") LocalDate weekStartDate,
            @Param("weekEndDate") LocalDate weekEndDate
    );

    int countCompletedQuestsByGuildAndWeek(
            @Param("guildId") Long guildId,
            @Param("weekStartDate") LocalDate weekStartDate,
            @Param("weekEndDate") LocalDate weekEndDate
    );

    List<DailyCountRow> findDailyCompletedQuests(
            @Param("guildId") Long guildId,
            @Param("weekStartDate") LocalDate weekStartDate,
            @Param("weekEndDate") LocalDate weekEndDate
    );

    int countQuestsByBattleId(@Param("battleId") Long battleId);

    int countCompletedQuestsByBattleId(@Param("battleId") Long battleId);

    int countBattleConditionsByBattleId(@Param("battleId") Long battleId);

    int countCompletedBattleConditionsByBattleId(@Param("battleId") Long battleId);
}
