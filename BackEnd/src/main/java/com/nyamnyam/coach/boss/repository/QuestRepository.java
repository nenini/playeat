package com.nyamnyam.coach.boss.repository;

import com.nyamnyam.coach.boss.entity.Quest;
import com.nyamnyam.coach.boss.repository.row.QuestBattleRow;
import com.nyamnyam.coach.boss.repository.row.QuestContributionRow;
import com.nyamnyam.coach.boss.repository.row.QuestGuildMemberRow;
import com.nyamnyam.coach.boss.repository.row.QuestRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface QuestRepository {

    List<QuestRow> findQuestsByBattleId(
            @Param("battleId") Long battleId,
            @Param("currentUserId") Long currentUserId
    );

    Optional<QuestRow> findMyQuestByBattleId(
            @Param("battleId") Long battleId,
            @Param("userId") Long userId
    );

    Optional<QuestRow> findQuestDetailById(
            @Param("questId") Long questId,
            @Param("currentUserId") Long currentUserId
    );

    Optional<Quest> findQuestByBattleIdAndUserId(
            @Param("battleId") Long battleId,
            @Param("userId") Long userId
    );

    void insertQuest(Quest quest);

    boolean existsQuestByBattleIdAndUserId(
            @Param("battleId") Long battleId,
            @Param("userId") Long userId
    );

    int countQuestsByBattleId(@Param("battleId") Long battleId);

    Optional<QuestBattleRow> findBattleById(@Param("battleId") Long battleId);

    Optional<Long> findGuildIdByBattleId(@Param("battleId") Long battleId);

    boolean existsActiveGuildMember(
            @Param("guildId") Long guildId,
            @Param("userId") Long userId
    );

    Optional<String> findGuildRole(
            @Param("guildId") Long guildId,
            @Param("userId") Long userId
    );

    List<QuestGuildMemberRow> findActiveGuildMembers(@Param("guildId") Long guildId);

    List<QuestContributionRow> findQuestContributionsByBattleId(
            @Param("battleId") Long battleId,
            @Param("currentUserId") Long currentUserId
    );
}
