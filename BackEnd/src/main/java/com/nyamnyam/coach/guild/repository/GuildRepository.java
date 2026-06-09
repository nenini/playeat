package com.nyamnyam.coach.guild.repository;

import com.nyamnyam.coach.guild.entity.Guild;
import com.nyamnyam.coach.guild.entity.GuildMember;
import com.nyamnyam.coach.guild.repository.row.GuildDetailRow;
import com.nyamnyam.coach.guild.repository.row.GuildMemberRow;
import com.nyamnyam.coach.guild.repository.row.GuildStatusRow;
import com.nyamnyam.coach.guild.repository.row.GuildSummaryRow;
import com.nyamnyam.coach.guild.repository.row.MyGuildRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface GuildRepository {

    boolean existsByName(@Param("name") String name);

    boolean existsByInviteCode(@Param("inviteCode") String inviteCode);

    void save(Guild guild);

    void saveMember(GuildMember guildMember);

    Optional<Guild> findById(@Param("guildId") Long guildId);

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
}
