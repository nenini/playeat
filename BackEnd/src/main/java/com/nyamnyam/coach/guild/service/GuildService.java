package com.nyamnyam.coach.guild.service;

import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AuthErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.GuildErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.UserErrorCode;
import com.nyamnyam.coach.guild.dto.request.GuildCreateRequest;
import com.nyamnyam.coach.guild.dto.response.GuildCreateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildDetailResponse;
import com.nyamnyam.coach.guild.dto.response.GuildListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberResponse;
import com.nyamnyam.coach.guild.dto.response.GuildSummaryResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildListResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildStatusResponse;
import com.nyamnyam.coach.guild.entity.Guild;
import com.nyamnyam.coach.guild.entity.GuildMember;
import com.nyamnyam.coach.guild.entity.GuildRole;
import com.nyamnyam.coach.guild.entity.GuildStatus;
import com.nyamnyam.coach.guild.entity.GuildVisibility;
import com.nyamnyam.coach.guild.entity.MyGuildJoinStatus;
import com.nyamnyam.coach.guild.repository.GuildRepository;
import com.nyamnyam.coach.guild.repository.row.GuildDetailRow;
import com.nyamnyam.coach.guild.repository.row.GuildMemberRow;
import com.nyamnyam.coach.guild.repository.row.GuildSummaryRow;
import com.nyamnyam.coach.guild.repository.row.MyGuildRow;
import com.nyamnyam.coach.user.entity.User;
import com.nyamnyam.coach.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuildService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int DEFAULT_MAX_MEMBERS = 30;
    private static final int MIN_MAX_MEMBERS = 1;
    private static final int MAX_MAX_MEMBERS = 30;
    private static final int INVITE_CODE_RETRY_LIMIT = 5;
    private static final String INVITE_CODE_PREFIX = "NYAM-";
    private static final char[] INVITE_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final String ACTIVE_STATUS = "ACTIVE";

    private final GuildRepository guildRepository;
    private final UserRepository userRepository;
    private final GuildValidator guildValidator;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional(readOnly = true)
    public GuildListResponse getGuilds(Long userId, Integer page, Integer size, String keyword, String sort) {
        findActiveUser(userId);

        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        int limit = normalizedSize + 1;
        int offset = normalizedPage * normalizedSize;
        String normalizedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        String normalizedSort = normalizeSort(sort);
        boolean alreadyJoinedAnyGuild = guildRepository.existsActiveMembershipByUserId(userId);

        List<GuildSummaryRow> rows = guildRepository.findActiveGuildSummaries(
                userId,
                normalizedKeyword,
                normalizedSort,
                limit,
                offset
        );

        boolean hasNext = rows.size() > normalizedSize;
        List<GuildSummaryResponse> guilds = rows.stream()
                .limit(normalizedSize)
                .map(row -> toGuildSummaryResponse(row, alreadyJoinedAnyGuild))
                .toList();

        return new GuildListResponse(guilds, normalizedPage, normalizedSize, hasNext);
    }

    @Transactional
    public GuildCreateResponse createGuild(Long userId, GuildCreateRequest request) {
        findActiveUser(userId);
        guildValidator.validateNotJoinedAnyGuild(userId);
        guildValidator.validateNoPendingJoinRequest(userId);
        validateGuildNameAvailable(request.name());

        int maxMembers = resolveMaxMembers(request.maxMembers());
        String inviteCode = generateInviteCode();

        Guild guild = Guild.builder()
                .name(request.name().trim())
                .description(normalizeDescription(request.description()))
                .inviteCode(inviteCode)
                .ownerUserId(userId)
                .maxMembers(maxMembers)
                .guildPoint(0)
                .visibility(GuildVisibility.PRIVATE.name())
                .status(GuildStatus.ACTIVE.name())
                .build();

        guildRepository.save(guild);

        GuildMember ownerMember = GuildMember.builder()
                .guildId(guild.getGuildId())
                .userId(userId)
                .role(GuildRole.OWNER.name())
                .build();
        guildRepository.saveMember(ownerMember);

        Guild savedGuild = guildValidator.validateGuildActive(guild.getGuildId());
        int memberCount = guildRepository.countActiveMembers(savedGuild.getGuildId());

        return new GuildCreateResponse(
                savedGuild.getGuildId(),
                savedGuild.getName(),
                savedGuild.getDescription(),
                savedGuild.getInviteCode(),
                savedGuild.getOwnerUserId(),
                GuildRole.OWNER.name(),
                memberCount,
                savedGuild.getMaxMembers(),
                savedGuild.getVisibility(),
                savedGuild.getStatus(),
                savedGuild.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public MyGuildListResponse getMyGuilds(Long userId) {
        findActiveUser(userId);

        List<MyGuildResponse> guilds = guildRepository.findMyActiveGuilds(userId)
                .stream()
                .map(this::toMyGuildResponse)
                .toList();

        return new MyGuildListResponse(guilds);
    }

    @Transactional(readOnly = true)
    public MyGuildStatusResponse getMyGuildStatus(Long userId) {
        findActiveUser(userId);
        return guildValidator.getMyGuildStatus(userId);
    }

    @Transactional(readOnly = true)
    public GuildDetailResponse getGuildDetail(Long guildId, Long userId) {
        findActiveUser(userId);
        guildValidator.validateGuildMember(guildId, userId);

        GuildDetailRow row = guildRepository.findGuildDetail(guildId, userId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_NOT_FOUND));

        return toGuildDetailResponse(row);
    }

    @Transactional(readOnly = true)
    public GuildMemberListResponse getGuildMembers(Long guildId, Long userId) {
        findActiveUser(userId);
        guildValidator.validateGuildMember(guildId, userId);

        List<GuildMemberResponse> members = guildRepository.findActiveMembers(guildId)
                .stream()
                .map(row -> toGuildMemberResponse(row, userId))
                .toList();

        return new GuildMemberListResponse(members);
    }

    private User findActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        if (!ACTIVE_STATUS.equals(user.getStatus())) {
            throw new BusinessException(AuthErrorCode.USER_INACTIVE);
        }
        return user;
    }

    private void validateGuildNameAvailable(String name) {
        if (guildRepository.existsByName(name.trim())) {
            throw new BusinessException(GuildErrorCode.GUILD_NAME_DUPLICATED);
        }
    }

    private int resolveMaxMembers(Integer maxMembers) {
        int resolved = maxMembers == null ? DEFAULT_MAX_MEMBERS : maxMembers;
        if (resolved < MIN_MAX_MEMBERS || resolved > MAX_MAX_MEMBERS) {
            throw new BusinessException(GuildErrorCode.GUILD_MAX_MEMBERS_INVALID);
        }
        return resolved;
    }

    private String generateInviteCode() {
        for (int attempt = 0; attempt < INVITE_CODE_RETRY_LIMIT; attempt++) {
            String inviteCode = INVITE_CODE_PREFIX + randomCodeSuffix();
            if (!guildRepository.existsByInviteCode(inviteCode)) {
                return inviteCode;
            }
        }
        throw new BusinessException(GuildErrorCode.INVITE_CODE_GENERATION_FAILED);
    }

    private String randomCodeSuffix() {
        StringBuilder builder = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            builder.append(INVITE_CODE_CHARS[secureRandom.nextInt(INVITE_CODE_CHARS.length)]);
        }
        return builder.toString();
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 0) {
            return DEFAULT_PAGE;
        }
        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    private String normalizeSort(String sort) {
        if ("createdAt".equals(sort)) {
            return "createdAt";
        }
        return "guildPoint";
    }

    private String normalizeDescription(String description) {
        return StringUtils.hasText(description) ? description.trim() : null;
    }

    private GuildSummaryResponse toGuildSummaryResponse(
            GuildSummaryRow row,
            boolean alreadyJoinedAnyGuild
    ) {
        return new GuildSummaryResponse(
                row.getGuildId(),
                row.getName(),
                row.getDescription(),
                row.getInviteCode(),
                row.getMemberCount(),
                row.getMaxMembers(),
                row.getGuildPoint(),
                row.getOwnerNickname(),
                resolveJoinStatus(row),
                row.getPendingRequestId(),
                alreadyJoinedAnyGuild
        );
    }

    private MyGuildJoinStatus resolveJoinStatus(GuildSummaryRow row) {
        if (row.getJoinedGuildId() != null) {
            return MyGuildJoinStatus.JOINED;
        }
        if (row.getPendingRequestId() != null) {
            return MyGuildJoinStatus.PENDING;
        }
        return MyGuildJoinStatus.NONE;
    }

    private MyGuildResponse toMyGuildResponse(MyGuildRow row) {
        return new MyGuildResponse(
                row.getGuildId(),
                row.getName(),
                row.getDescription(),
                row.getInviteCode(),
                row.getMemberCount(),
                row.getMaxMembers(),
                row.getGuildPoint(),
                row.getMyRole(),
                row.getJoinedAt()
        );
    }

    private GuildDetailResponse toGuildDetailResponse(GuildDetailRow row) {
        return new GuildDetailResponse(
                row.getGuildId(),
                row.getName(),
                row.getDescription(),
                row.getInviteCode(),
                row.getOwnerUserId(),
                row.getOwnerNickname(),
                row.getMemberCount(),
                row.getMaxMembers(),
                row.getGuildPoint(),
                row.getVisibility(),
                row.getStatus(),
                row.getMyRole(),
                row.getCreatedAt(),
                row.getUpdatedAt()
        );
    }

    private GuildMemberResponse toGuildMemberResponse(GuildMemberRow row, Long userId) {
        return new GuildMemberResponse(
                row.getMemberId(),
                row.getUserId(),
                row.getNickname(),
                row.getProfileImageUrl(),
                row.getCharacterId(),
                row.getCharacterName(),
                row.getCharacterLevel(),
                row.getCharacterStage(),
                row.getCharacterMood(),
                row.getCharacterAppearanceType(),
                row.getRole(),
                row.getJoinedAt(),
                row.getUserId().equals(userId)
        );
    }
}
