package com.nyamnyam.coach.boss.repository;

import com.nyamnyam.coach.boss.entity.Quest;
import com.nyamnyam.coach.boss.entity.QuestVerification;
import com.nyamnyam.coach.boss.entity.RewardClaim;
import com.nyamnyam.coach.boss.repository.row.BattleConditionStateRow;
import com.nyamnyam.coach.boss.repository.row.BattleStateRow;
import com.nyamnyam.coach.boss.repository.row.DietVerificationRow;
import com.nyamnyam.coach.boss.repository.row.QuestBattleRow;
import com.nyamnyam.coach.boss.repository.row.QuestContributionRow;
import com.nyamnyam.coach.boss.repository.row.QuestGuildMemberRow;
import com.nyamnyam.coach.boss.repository.row.QuestRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    Optional<Quest> findQuestForUpdate(@Param("questId") Long questId);

    int updateQuestCompleted(
            @Param("questId") Long questId,
            @Param("currentValue") int currentValue
    );

    int updateQuestRewarded(@Param("questId") Long questId);

    void insertQuestVerification(QuestVerification verification);

    boolean existsQuestVerificationByQuestId(@Param("questId") Long questId);

    Optional<DietVerificationRow> findTodayDietForVerification(
            @Param("userId") Long userId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    Optional<BattleStateRow> findBattleStateForUpdate(@Param("battleId") Long battleId);

    Optional<BattleStateRow> findBattleRewardInfo(@Param("battleId") Long battleId);

    int updateBattleDamage(
            @Param("battleId") Long battleId,
            @Param("damage") int damage
    );

    int updateBattleDefeated(@Param("battleId") Long battleId);

    void insertBossBattleDamageLog(
            @Param("battleId") Long battleId,
            @Param("userId") Long userId,
            @Param("damage") int damage,
            @Param("sourceType") String sourceType,
            @Param("sourceId") Long sourceId,
            @Param("description") String description
    );

    List<BattleConditionStateRow> findBattleConditionsForUpdate(@Param("battleId") Long battleId);

    int countSugarUnderLimitMemberDates(
            @Param("guildId") Long guildId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("thresholdValue") java.math.BigDecimal thresholdValue
    );

    int updateBattleConditionProgressValue(
            @Param("battleConditionId") Long battleConditionId,
            @Param("currentValue") int currentValue
    );

    int completeBattleCondition(
            @Param("battleConditionId") Long battleConditionId,
            @Param("currentValue") int currentValue
    );

    int countIncompleteBattleConditions(@Param("battleId") Long battleId);

    boolean existsRewardClaim(
            @Param("userId") Long userId,
            @Param("sourceType") String sourceType,
            @Param("sourceId") Long sourceId
    );

    void insertRewardClaim(RewardClaim rewardClaim);
}
