package com.nyamnyam.coach.guild.service;

import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.GuildErrorCode;
import com.nyamnyam.coach.guild.dto.response.MyGuildStatusResponse;
import com.nyamnyam.coach.guild.entity.Guild;
import com.nyamnyam.coach.guild.entity.GuildRole;
import com.nyamnyam.coach.guild.entity.GuildStatus;
import com.nyamnyam.coach.guild.entity.MyGuildJoinStatus;
import com.nyamnyam.coach.guild.repository.GuildRepository;
import com.nyamnyam.coach.guild.repository.row.GuildStatusRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GuildValidator {

    private final GuildRepository guildRepository;

    public Guild validateGuildExists(Long guildId) {
        return guildRepository.findById(guildId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_NOT_FOUND));
    }

    public Guild validateGuildActive(Long guildId) {
        Guild guild = validateGuildExists(guildId);
        if (!GuildStatus.ACTIVE.name().equals(guild.getStatus())) {
            throw new BusinessException(GuildErrorCode.GUILD_NOT_FOUND);
        }
        return guild;
    }

    public String validateGuildMember(Long guildId, Long userId) {
        validateGuildActive(guildId);
        return guildRepository.findActiveMemberRole(guildId, userId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_ACCESS_DENIED));
    }

    public void validateGuildOwner(Long guildId, Long userId) {
        String role = validateGuildMember(guildId, userId);
        if (!GuildRole.OWNER.name().equals(role)) {
            throw new BusinessException(GuildErrorCode.GUILD_OWNER_ONLY);
        }
    }

    public void validateGuildCapacity(Long guildId) {
        Guild guild = validateGuildActive(guildId);
        int memberCount = guildRepository.countActiveMembers(guildId);
        if (memberCount >= guild.getMaxMembers()) {
            throw new BusinessException(GuildErrorCode.GUILD_CAPACITY_EXCEEDED);
        }
    }

    public void validateNotJoinedAnyGuild(Long userId) {
        if (guildRepository.existsActiveMembershipByUserId(userId)) {
            throw new BusinessException(GuildErrorCode.USER_ALREADY_JOINED_GUILD);
        }
    }

    public void validateNoPendingJoinRequest(Long userId) {
        if (guildRepository.existsPendingJoinRequestByUserId(userId)) {
            throw new BusinessException(GuildErrorCode.USER_HAS_PENDING_GUILD_REQUEST);
        }
    }

    public MyGuildStatusResponse getMyGuildStatus(Long userId) {
        return guildRepository.findJoinedStatus(userId)
                .map(this::toJoinedStatus)
                .orElseGet(() -> guildRepository.findPendingStatus(userId)
                        .map(this::toPendingStatus)
                        .orElseGet(this::toNoneStatus));
    }

    private MyGuildStatusResponse toJoinedStatus(GuildStatusRow row) {
        return new MyGuildStatusResponse(
                MyGuildJoinStatus.JOINED,
                new MyGuildStatusResponse.GuildStatusInfo(
                        row.getGuildId(),
                        row.getName(),
                        row.getInviteCode(),
                        row.getRole()
                ),
                null
        );
    }

    private MyGuildStatusResponse toPendingStatus(GuildStatusRow row) {
        return new MyGuildStatusResponse(
                MyGuildJoinStatus.PENDING,
                new MyGuildStatusResponse.GuildStatusInfo(
                        row.getGuildId(),
                        row.getName(),
                        row.getInviteCode(),
                        null
                ),
                new MyGuildStatusResponse.JoinRequestInfo(
                        row.getRequestId(),
                        row.getRequestStatus(),
                        row.getRequestCreatedAt()
                )
        );
    }

    private MyGuildStatusResponse toNoneStatus() {
        return new MyGuildStatusResponse(MyGuildJoinStatus.NONE, null, null);
    }
}
