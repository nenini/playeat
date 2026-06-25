package com.nyamnyam.coach.guild.repository;

import com.nyamnyam.coach.guild.entity.Guild;
import com.nyamnyam.coach.guild.entity.GuildJoinRequest;
import com.nyamnyam.coach.guild.entity.GuildMember;
import com.nyamnyam.coach.guild.repository.row.GuildDetailRow;
import com.nyamnyam.coach.guild.repository.row.GuildMemberRow;
import com.nyamnyam.coach.guild.repository.row.GuildNoticeRow;
import com.nyamnyam.coach.guild.repository.row.GuildStatusRow;
import com.nyamnyam.coach.guild.repository.row.GuildSummaryRow;
import com.nyamnyam.coach.guild.repository.row.JoinRequestRow;
import com.nyamnyam.coach.guild.repository.row.MyGuildRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface GuildRepository {

    boolean existsByName(@Param("name") String name);

    boolean existsByNameExceptGuildId(
            @Param("name") String name,
            @Param("guildId") Long guildId
    );

    boolean existsByInviteCode(@Param("inviteCode") String inviteCode);

    void save(Guild guild);

    void saveMember(GuildMember guildMember);

    Optional<Guild> findById(@Param("guildId") Long guildId);

    Optional<Guild> findActiveGuildByInviteCode(@Param("inviteCode") String inviteCode);

    Optional<Long> findGuildOwnerUserId(@Param("guildId") Long guildId);

    List<GuildSummaryRow> findActiveGuildSummaries(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("sort") String sort,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    List<MyGuildRow> findMyActiveGuilds(@Param("userId") Long userId);

    Optional<GuildStatusRow> findJoinedStatus(@Param("userId") Long userId);

    Optional<GuildStatusRow> findPendingStatus(@Param("userId") Long userId);

    Optional<GuildDetailRow> findGuildDetail(
            @Param("guildId") Long guildId,
            @Param("userId") Long userId
    );

    List<GuildMemberRow> findActiveMembers(@Param("guildId") Long guildId);

    Optional<GuildMemberRow> findActiveMemberByGuildIdAndUserId(
            @Param("guildId") Long guildId,
            @Param("userId") Long userId
    );

    Optional<GuildMemberRow> findMemberByGuildIdAndUserId(
            @Param("guildId") Long guildId,
            @Param("userId") Long userId
    );

    int reactivateGuildMember(
            @Param("guildId") Long guildId,
            @Param("userId") Long userId
    );

    Optional<GuildMemberRow> findActiveMemberByMemberId(
            @Param("guildId") Long guildId,
            @Param("memberId") Long memberId
    );

    Optional<GuildMemberRow> findMemberByMemberId(
            @Param("guildId") Long guildId,
            @Param("memberId") Long memberId
    );

    Optional<GuildMemberRow> findMemberDetail(
            @Param("guildId") Long guildId,
            @Param("memberId") Long memberId,
            @Param("currentUserId") Long currentUserId
    );

    int countMemberRecordedDays(
            @Param("guildId") Long guildId,
            @Param("userId") Long userId,
            @Param("weekStartDate") LocalDate weekStartDate,
            @Param("weekEndDate") LocalDate weekEndDate
    );

    Optional<Long> findCurrentOrLatestBattleIdByGuildId(@Param("guildId") Long guildId);

    int sumMemberCompletedQuestDamage(
            @Param("battleId") Long battleId,
            @Param("userId") Long userId
    );

    int countMemberCompletedQuests(
            @Param("battleId") Long battleId,
            @Param("userId") Long userId
    );

    Optional<String> findActiveMemberRole(
            @Param("guildId") Long guildId,
            @Param("userId") Long userId
    );

    boolean existsActiveMember(
            @Param("guildId") Long guildId,
            @Param("userId") Long userId
    );

    boolean existsActiveMembershipByUserId(@Param("userId") Long userId);

    boolean existsPendingJoinRequestByUserId(@Param("userId") Long userId);

    int countActiveMembers(@Param("guildId") Long guildId);

    int updateGuild(
            @Param("guildId") Long guildId,
            @Param("name") String name,
            @Param("description") String description,
            @Param("maxMembers") int maxMembers
    );

    int softDeleteGuild(@Param("guildId") Long guildId);

    int markAllMembersLeft(@Param("guildId") Long guildId);

    int leaveGuild(
            @Param("guildId") Long guildId,
            @Param("userId") Long userId
    );

    int kickGuildMember(
            @Param("guildId") Long guildId,
            @Param("memberId") Long memberId
    );

    List<GuildNoticeRow> findGuildNotices(@Param("guildId") Long guildId);

    Optional<GuildNoticeRow> findGuildNoticeById(
            @Param("guildId") Long guildId,
            @Param("noticeId") Long noticeId
    );

    void saveGuildNotice(GuildNoticeRow guildNotice);

    int updateGuildNotice(
            @Param("guildId") Long guildId,
            @Param("noticeId") Long noticeId,
            @Param("title") String title,
            @Param("content") String content
    );

    int deleteGuildNotice(
            @Param("guildId") Long guildId,
            @Param("noticeId") Long noticeId
    );

    void insertJoinRequest(GuildJoinRequest joinRequest);

    List<JoinRequestRow> findMyJoinRequests(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    List<JoinRequestRow> findGuildJoinRequests(
            @Param("guildId") Long guildId,
            @Param("status") String status,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    Optional<JoinRequestRow> findJoinRequestByGuildIdAndRequestId(
            @Param("guildId") Long guildId,
            @Param("requestId") Long requestId
    );

    int approveJoinRequest(
            @Param("guildId") Long guildId,
            @Param("requestId") Long requestId,
            @Param("handledBy") Long handledBy
    );

    int rejectJoinRequest(
            @Param("guildId") Long guildId,
            @Param("requestId") Long requestId,
            @Param("handledBy") Long handledBy
    );

    int cancelJoinRequest(
            @Param("guildId") Long guildId,
            @Param("requestId") Long requestId,
            @Param("handledBy") Long handledBy
    );

    int cancelOtherPendingRequests(
            @Param("userId") Long userId,
            @Param("excludeRequestId") Long excludeRequestId,
            @Param("handledBy") Long handledBy
    );
}
