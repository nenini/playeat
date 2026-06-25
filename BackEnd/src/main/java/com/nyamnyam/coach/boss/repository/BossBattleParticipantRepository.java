package com.nyamnyam.coach.boss.repository;

import com.nyamnyam.coach.boss.entity.BossBattleParticipant;
import com.nyamnyam.coach.boss.repository.row.BossBattleParticipantCountRow;
import com.nyamnyam.coach.boss.repository.row.BossBattleParticipantRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BossBattleParticipantRepository {

    List<BossBattleParticipantRow> findActiveGuildMembersForBattleSnapshot(@Param("guildId") Long guildId);

    void insertBossBattleParticipant(BossBattleParticipant participant);

    int countActiveParticipantsByBattleId(@Param("battleId") Long battleId);

    BossBattleParticipantCountRow countParticipantsByBattleId(@Param("battleId") Long battleId);

    List<BossBattleParticipantRow> findParticipantsByBattleId(@Param("battleId") Long battleId);

    List<BossBattleParticipantRow> findActiveParticipantsByBattleId(@Param("battleId") Long battleId);

    boolean existsActiveParticipant(
            @Param("battleId") Long battleId,
            @Param("userId") Long userId
    );

    Optional<BossBattleParticipantRow> findParticipantByBattleIdAndUserId(
            @Param("battleId") Long battleId,
            @Param("userId") Long userId
    );

    int markParticipantLeftByGuildAndUser(
            @Param("guildId") Long guildId,
            @Param("userId") Long userId
    );

    int markParticipantKickedByGuildAndUser(
            @Param("guildId") Long guildId,
            @Param("userId") Long userId
    );
}
